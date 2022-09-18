package com.spring.boot.upload.example.concurrent.controller;

import com.spring.boot.upload.example.concurrent.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FileController {

    @Autowired
    FileService fileService;

    @GetMapping("/")
    public  String index()
    {
        return "upload";
    }


    @PostMapping("/files")
    public String uploadFiles(@RequestParam("files")List<MultipartFile> files, RedirectAttributes redirectAttributes) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
     //   long startTime = System.currentTimeMillis();
        fileService.uploadFilesSequentially(files);
       // long endTime = System.currentTimeMillis();
        stopWatch.stop();
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded all files! Time Taken By Single Thread : "  +
                        stopWatch.getTotalTimeNanos());
        return "redirect:/";
    }

    @PostMapping("/fastupload")
    public String uploadFilesConcurrently(@RequestParam("files") List<MultipartFile> multipartFiles, RedirectAttributes redirectAttributes) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        fileService.uploadFilesConcurrently(multipartFiles);
        stopWatch.stop();
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded all files! Time Taken By Multiple Threads : "  +
                        stopWatch.getTotalTimeNanos());

        return "redirect:/";
    }
}
