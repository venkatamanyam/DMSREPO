package com.example.Doc_Prac.controller;

import com.example.Doc_Prac.model.DocMeta;
import com.example.Doc_Prac.service.DocMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/doc/data")
@CrossOrigin("*")
public class DocMetaController {
    @Autowired
    DocMetaService docMetaService;

    // http://localhost:8082/doc/data/add
    @PostMapping("/add")
    public ResponseEntity<?> addData(@RequestBody DocMeta docMeta){
        return new ResponseEntity<>(docMetaService.addData(docMeta), HttpStatus.OK);
    }

    // http://localhost:8082/doc/data/file/add
    @PutMapping("/file/add")
    public ResponseEntity<?> addImage(@RequestParam("id") int id,@RequestPart("file") MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;
        String[] extension = fileName.split("\\.");

        if(extension[1].equalsIgnoreCase("jpg") || extension[1].equalsIgnoreCase("jpeg") || extension[1].equalsIgnoreCase("pdf") || extension[1].equalsIgnoreCase("zip")){
            return new ResponseEntity<>(docMetaService.addFile(id,multipartFile),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(docMetaService.deleteData(id),HttpStatus.BAD_REQUEST);
        }
    }

    // http://localhost:8082/doc/data/delete/
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteData(@PathVariable int id){
        return new ResponseEntity<>(docMetaService.deleteData(id),HttpStatus.BAD_REQUEST);
    }

    // http://localhost:8082/doc/data/search/file/
    @GetMapping(value = "/search/file/{docTags}", produces = "application/json")
    public ResponseEntity<?> searchFiles(@PathVariable String docTags){
        return new ResponseEntity<>(docMetaService.searchDocuments(docTags), HttpStatus.OK);
    }

    // http://localhost:8082/doc/data/open/file/
    @GetMapping("/open/file/{docTitle}")
    public ResponseEntity<?> openFile(@PathVariable String docTitle) throws IOException {
        String mediaType = "";
        String[] extension = docTitle.split("\\.");

        if(extension[1].equalsIgnoreCase("pdf") || extension[1].equalsIgnoreCase("zip")){
            mediaType="application/";
        }
        else{
            mediaType="image/";
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf(mediaType+extension[1]))
                .body(docMetaService.openFile(docTitle));
    }

    // http://localhost:8082/doc/data/get/part/data/
    @GetMapping("/get/part/data/{userGroup}")
    public ResponseEntity<?> getParticularDeptData(@PathVariable String userGroup){
        return new ResponseEntity<>(docMetaService.getParticularDeptDetails(userGroup),HttpStatus.OK);
    }

    // http://localhost:8082/doc/data/get/doctitles/
//    @GetMapping("/get/doctitles/{userID}")
//    public ResponseEntity<?> getDocTitles(@PathVariable String userID){
//        return new ResponseEntity<>(docMetaService.getDocTitles(userID),HttpStatus.OK);
//    }

    // http://localhost:8082/doc/data/del/file/
    @DeleteMapping("/del/file/{docTitle}")
    public ResponseEntity<?> deleteFile(@PathVariable String docTitle){
        return new ResponseEntity<>(docMetaService.deleteFile(docTitle),HttpStatus.OK);
    }

    // http://localhost:8082/doc/data/get/docs/details/
    @GetMapping("/get/docs/details/{docTags}")
    public ResponseEntity<?> getAllDocsDetails(@PathVariable String docTags){
        return new ResponseEntity<>(docMetaService.getAllDocsByDocTags(docTags),HttpStatus.OK);
    }

}
