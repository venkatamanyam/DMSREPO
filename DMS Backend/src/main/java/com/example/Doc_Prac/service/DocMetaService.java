package com.example.Doc_Prac.service;

import com.example.Doc_Prac.model.DocMeta;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocMetaService {
    public DocMeta addData(DocMeta docMeta);
    public String addFile(String ticketID, MultipartFile multipartFile) throws IOException;
    public String generateDocID();
    public String deleteData(int id);
    public String updateFile(int id, MultipartFile multipartFile) throws IOException;
    public byte[] openFile(String docTitle) throws IOException;
    public  List<DocMeta> getParticularDeptDetails(String userGroup);
    public List<String> getDocTitles(String userId);
    public String deleteFile(String docTitle);
    public List<DocMeta> getAllDocsByDocTags(String docTags);
}
