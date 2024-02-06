package io.learnk8s.knotejava.services;

import io.learnk8s.knotejava.dto.Note;
import io.learnk8s.knotejava.properties.KnoteProperties;
import io.learnk8s.knotejava.repositories.NotesRepository;
import io.minio.*;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    @Autowired
    private NotesRepository notesRepository;
    @Autowired
    private KnoteProperties knoteProperties;
    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();
    private MinioClient minioClient;

    public InputStream getImageByName(String name) throws Exception{

        return minioClient.getObject(GetObjectArgs.builder()
                        .bucket(knoteProperties.getMinioBucket())
                                .object(name)
                                        .build());
    }
    public void getAllNotes(Model model) {
        List<Note> notes = notesRepository.findAll();
        Collections.reverse(notes);
        model.addAttribute("notes", notes);
    }

    public void saveNote(String description, Model model){
        if(description != null && !description.trim().isEmpty()){
            Node document = parser.parse(description.trim());
            String html = renderer.render(document);
            notesRepository.save(new Note(null, html));
            //After publish you need to clean up the textarea
            model.addAttribute("description", "");
        }
    }

    public void uploadImage(MultipartFile file, String description, Model model) throws Exception {
        String fileId = UUID.randomUUID().toString() + "." + file.getOriginalFilename().split("\\.")[1];
        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(knoteProperties.getMinioBucket())
                        .object(fileId)
                        .stream(file.getInputStream(), file.getSize(), 20971520)
                        .contentType(file.getContentType())
                        .build());
        model.addAttribute("description", description + " ![](/img/" + fileId + ")");
    }

    public void initMinio() throws InterruptedException {
        boolean success = false;
        while (!success) {
            try {
                minioClient = MinioClient.builder()
                        .endpoint("http://" + knoteProperties.getMinioHost() + ":9000")
                        .credentials(knoteProperties.getMinioAccessKey(), knoteProperties.getMinioSecretKey())
                        .build();
                // Check if the bucket already exists.
                boolean isExist = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(knoteProperties.getMinioBucket())
                                .build()
                );
                if (isExist) {
                    System.out.println("> Bucket already exists.");
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(knoteProperties.getMinioBucket())
                            .build());
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("> Minio Reconnect: " + knoteProperties.isMinioReconnectEnabled());
                if (knoteProperties.isMinioReconnectEnabled()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    success = true;
                }
            }
        }
        System.out.println("> Minio initialized!");
    }

}
