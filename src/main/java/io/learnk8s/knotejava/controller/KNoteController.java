package io.learnk8s.knotejava.controller;

import io.learnk8s.knotejava.properties.KnoteProperties;
import io.learnk8s.knotejava.services.NoteService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

@Controller
@EnableConfigurationProperties(KnoteProperties.class)
public class KNoteController {

    @Autowired
    NoteService noteService;

    @PostConstruct
    public void init() throws InterruptedException{
        noteService.initMinio();
    }

    @GetMapping(value = "/img/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImageByName(@PathVariable String name) throws Exception {
        InputStream imageStream = noteService.getImageByName(name);
        return IOUtils.toByteArray(imageStream);
    }

    @GetMapping("/")
    public String index(Model model){
        noteService.getAllNotes(model);
        return "index";
    }

    @PostMapping("/note")
    public String saveNote(@RequestParam("image") MultipartFile file,
                           @RequestParam String description,
                           @RequestParam(required = false) String publish,
                           @RequestParam(required = false) String upload,
                           Model model) throws Exception {
        if(publish != null && publish.equals("Publish")){
            noteService.saveNote(description, model);
            noteService.getAllNotes(model);
            return "redirect:/";
        }
        if (upload != null && upload.equals("Upload")) {
            if (file != null && file.getOriginalFilename() != null
                    && !file.getOriginalFilename().isEmpty()) {
                noteService.uploadImage(file, description, model);
            }
            noteService.getAllNotes(model);
            return "index";
        }
        return "index";
    }

}
