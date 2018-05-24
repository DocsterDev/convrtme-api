package com.convrt.controller;

import com.convrt.data.model.FileAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/files2")
public class FileController {

    @GetMapping
    public Set<FileAttribute> allFiles() {
        Set<FileAttribute> files = new HashSet<>();

        FileAttribute fa1 = new FileAttribute();
        fa1.setName("testname.mp4");

        files.add(fa1);

        return files;
    }
}
