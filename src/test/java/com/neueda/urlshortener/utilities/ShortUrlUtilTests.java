package com.neueda.urlshortener.utilities;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ShortUrlUtilTests {
    public List<String> readFile(String filePath) throws Exception {
        List<String> input = new ArrayList<>();
        String fileLine;
        try {
            InputStreamReader streamReader;
            InputStream inputStream;
            if(Paths.get(filePath).isAbsolute()) {
                inputStream =  new FileInputStream(filePath);
            }
            else {
                inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            }
            if(inputStream != null) {
                streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                while ((fileLine = bufferedReader.readLine()) != null) {
                    input.add(fileLine);
                }
            }
            else
                throw new Exception(ShortUrlConstants.FILE_NOT_FOUND_EX_MESSAGE);

        } catch (FileNotFoundException ex) {
           throw new Exception(ShortUrlConstants.FILE_NOT_FOUND_EX_MESSAGE + ex.getMessage());

        } catch (Exception ex) {
            throw new Exception(ShortUrlConstants.GENERAL_EX_MESSAGE + ex.getMessage());

        }
        return input;
    }
}
