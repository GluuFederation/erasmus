package org.xdi.oxd.badgemanager.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Arvind Tomar on 25/10/16.
 */
@Controller
public class ImageService {


    private String opHost;
    public static final String root = "img";
    public static final String organization = "organizations";

    public static String saveImage(String path, MultipartFile uploadfile) throws IOException {

        // Get the filename and build the local file path (be sure that the
        // application have write permissions on such directory)
        File Img = new File(new ClassPathXmlApplicationContext().getResource(root).getURI());
        if (!Img.exists()) {
            Img.mkdirs();
        }
        File subDir = new File(new ClassPathXmlApplicationContext().getResource(root).getFile().getPath() + "/" + path);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }
        String filename = uploadfile.getOriginalFilename();
        String filepath = (new ClassPathXmlApplicationContext().getResource(root).getFile().getPath()) + "/" + path + "/" + filename;
        // Save the file locally
        BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(new File(filepath)));
        stream.write(uploadfile.getBytes());
        stream.close();
        return "" + new ClassPathXmlApplicationContext().getResource(root + "/" + path + "/" + filename).getURL();


    }
}
