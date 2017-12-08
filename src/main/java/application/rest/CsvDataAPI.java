package application.rest;


import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class CsvDataAPI {

    Gson gson = new Gson();


    /**
     * Takes the filename of a jpeg, and a number of sections
     * and returns a comma delimited String for use in a csv file
     * with comma at end
     * @param filename    The name of a file to be read as a jpeg
     * @param sections    The dimension of square grid to interpret photo
     * @return            A comma delimited String suitable for a csv file
     */
    public static String imageToRow(String filename, int sections)
    {
        String output="";
        try {
            File file=new File(filename);
            BufferedImage image= ImageIO.read(file);

        //    output+=file.getName()+",";

            Raster raster=image.getData();
            int imageHeight=raster.getHeight();
            int imageWidth=raster.getWidth();
            int Red[][] = new int[sections][sections];
            int Green[][] = new int[sections][sections];
            int Blue[][] = new int[sections][sections];

            int pixelsVerticalPerSection=imageHeight/sections;
            int pixelsHorizontalPerSection= imageWidth/sections;

            // Starting at the top left corner (0,0) lets process each section
            for(int gridRow=0;gridRow<sections;gridRow++)
            {
                for(int gridCol=0;gridCol<sections;gridCol++)
                {
                    long redTotalperSection = 0;
                    long greenTotalperSection = 0;
                    long blueTotalperSection = 0;
                    int pixelsProcessedPerSection =0;
                    int pixelRowStart=gridRow*pixelsVerticalPerSection;
                    int pixelRowStop=(gridRow+1)*pixelsVerticalPerSection;
                    int pixelColStart=gridCol*pixelsHorizontalPerSection;
                    int pixelColStop=(gridCol+1)*pixelsHorizontalPerSection;
                    //			System.out.println("checking ("+pixelColStart+","+pixelRowStart+") to ("+pixelColStop+","+pixelRowStop+")");
                    for(int pixelRow=pixelRowStart;pixelRow<pixelRowStop;pixelRow++)
                    {
                        for(int pixelCol=pixelColStart;pixelCol<pixelColStop;pixelCol++)
                        {
                            int currPixel=image.getRGB(pixelCol, pixelRow);
                            int red= (currPixel>>16)&255;
                            int green= (currPixel>>8)&255;
                            int blue= (currPixel)&255;
                            redTotalperSection+=red;
                            greenTotalperSection+=green;
                            blueTotalperSection+=blue;


                            pixelsProcessedPerSection++;

                        }

                    }

                    //	System.out.println("it contained "+pixelsProcessedPerSection+" pixels.");
                    Red[gridRow][gridCol]=(int)(redTotalperSection/pixelsProcessedPerSection);
                    Green[gridRow][gridCol]=(int)(greenTotalperSection/pixelsProcessedPerSection);
                    Blue[gridRow][gridCol]=(int)(blueTotalperSection/pixelsProcessedPerSection);
                    //		System.out.println("average RGB for this section was: "+Red[gridRow][gridCol]+","+Green[gridRow][gridCol]+","+Blue[gridRow][gridCol]);
                    output+=(""+Red[gridRow][gridCol]+","+Green[gridRow][gridCol]+","+Blue[gridRow][gridCol]+",");



                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return " Error";
        }
        return output;

    }

    /**
     * Creates a CSV file of input photos
     * @param carDir      Directory of car photos to be added to csv marked as car
     * @param noncarDir   Directory of noncar photos to be added to csv marked as noncar
     * @param outputcsv   The path to write the output csv file to
     * @param sections    The number of sections (to pass to imageToRow )
     */
    public static void createCSVFromDirectories(String carDir, String noncarDir,String outputcsv,int sections)
    {
        try {
            BufferedWriter	bw= new BufferedWriter(new FileWriter(new File(outputcsv)));
            //first write out file headers

          //  bw.write("filename,");

            for(int row=0;row<sections;row++)
            {
                for(int col=0;col<sections;col++)
                {
                    bw.write("red row "+row+" col "+col+",");
                    bw.write("green row "+row+" col "+col+",");
                    bw.write("blue row "+row+" col "+col+",");


                }


            }
            bw.write("car");
            bw.newLine();

            //then add rows for the cars
            final File cars = new File(carDir);
            for(final File car : cars.listFiles())
            {
                bw.write(imageToRow(car.getAbsolutePath(),sections)+"true");
                bw.newLine();
                System.out.println("car " + car + " added to csvfile");
            }
            //then add rows for the noncars
            final File noncars = new File(noncarDir);
            for(final File noncar : noncars.listFiles())
            {
                bw.write(imageToRow(noncar.getAbsolutePath(),sections)+"false");
                bw.newLine();
                System.out.println("noncar " + noncar + " added to csv file");
            }
            bw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Used to build csv file locally of cars to be used for model data in web app
     *
     * Uses hardcoded image paths and hardcoded sections value of 10
     */
    public static void main(String args[])
    {

        createCSVFromDirectories("C:\\images\\cars","C:\\images\\noncars","cars.csv",10);
          System.out.println("done");
    }

    /**
     * Takes the filename of a jpeg, and a number of sections
     * and returns a comma delimited String for use in a csv file
     * with comma at end
     * @param filename    The name of a file to be read as a jpeg
     * @param sections    The dimension of square grid to interpret photo

     * @return            A comma delimited String suitable for a csv file
     */
    public static String imageToRowFromLinks(String filename, int sections)
    {
        //System.out.println("imageToRowFromLinks called");
        BMConnObject cap = new BMConnObject();
        String output="";
        try {
            //File file=new File(filename);
            URL file = new URL(filename);
            BufferedImage image= ImageIO.read(file.openStream());
           // output+=file.getName()+",";
            Raster raster=image.getData();
            int imageHeight=raster.getHeight();
            int imageWidth=raster.getWidth();
            int Red[][] = new int[sections][sections];
            int Green[][] = new int[sections][sections];
            int Blue[][] = new int[sections][sections];

            int pixelsVerticalPerSection=imageHeight/sections;
            int pixelsHorizontalPerSection= imageWidth/sections;

            // Starting at the top left corner (0,0) lets process each section
            for(int gridRow=0;gridRow<sections;gridRow++)
            {
                for(int gridCol=0;gridCol<sections;gridCol++)
                {
                    long redTotalperSection = 0;
                    long greenTotalperSection = 0;
                    long blueTotalperSection = 0;
                    int pixelsProcessedPerSection =0;
                    int pixelRowStart=gridRow*pixelsVerticalPerSection;
                    int pixelRowStop=(gridRow+1)*pixelsVerticalPerSection;
                    int pixelColStart=gridCol*pixelsHorizontalPerSection;
                    int pixelColStop=(gridCol+1)*pixelsHorizontalPerSection;
                    //			System.out.println("checking ("+pixelColStart+","+pixelRowStart+") to ("+pixelColStop+","+pixelRowStop+")");
                    for(int pixelRow=pixelRowStart;pixelRow<pixelRowStop;pixelRow++)
                    {
                        for(int pixelCol=pixelColStart;pixelCol<pixelColStop;pixelCol++)
                        {
                            int currPixel=image.getRGB(pixelCol, pixelRow);
                            int red= (currPixel>>16)&255;
                            int green= (currPixel>>8)&255;
                            int blue= (currPixel)&255;
                            redTotalperSection+=red;
                            greenTotalperSection+=green;
                            blueTotalperSection+=blue;


                            pixelsProcessedPerSection++;

                        }

                    }

                    //	System.out.println("it contained "+pixelsProcessedPerSection+" pixels.");
                    Red[gridRow][gridCol]=(int)(redTotalperSection/pixelsProcessedPerSection);
                    Green[gridRow][gridCol]=(int)(greenTotalperSection/pixelsProcessedPerSection);
                    Blue[gridRow][gridCol]=(int)(blueTotalperSection/pixelsProcessedPerSection);
                    //		System.out.println("average RGB for this section was: "+Red[gridRow][gridCol]+","+Green[gridRow][gridCol]+","+Blue[gridRow][gridCol]);
                    output+=(""+Red[gridRow][gridCol]+","+Green[gridRow][gridCol]+","+Blue[gridRow][gridCol]+",");

                   // BufferedImage outputImage = new BufferedImage(sections, sections, BufferedImage.TYPE_INT_RGB);

                }

            }
            BufferedImage outputImage = new BufferedImage(sections, sections, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < sections; y++) {
                for (int x = 0; x < sections; x++) {
                    int rgb = Red[y][x];
                    rgb = (rgb << 8) + Green[y][x];
                    rgb = (rgb << 8) + Blue[y][x];
                    outputImage.setRGB(x, y, rgb);
                }
            }


            //Now lets make this buffered image into a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "jpg", baos );
            baos.flush();
            byte[] imageByteArray = baos.toByteArray();
            baos.close();

           cap.processImage(imageByteArray,"pixelized"+filename.substring(filename.lastIndexOf("/")+1,filename.length()));

        }
        catch(Exception e)
        {
            e.printStackTrace();
            return " Error";
        }
        return output;

    }

    /**
     * Creates a CSV file of input photos
     * @param carDir      Directory of car photos to be added to csv marked as car
     * @param noncarDir   Directory of noncar photos to be added to csv marked as noncar
     * @param outputcsv   The path to write the output csv file to
     * @param sections    The number of sections (to pass to imageToRow )

     */
    public static void createCSVFromLinks(String carDir, String noncarDir,String outputcsv,int sections)
    {
        try {
            BufferedWriter	bw= new BufferedWriter(new FileWriter(new File(outputcsv)));
            //first write out file headers
            bw.write("filename,");
            for(int row=0;row<sections;row++)
            {
                for(int col=0;col<sections;col++)
                {
                    bw.write("red row "+row+" col "+col+",");
                    bw.write("green row "+row+" col "+col+",");
                    bw.write("blue row "+row+" col "+col+",");


                }


            }
            bw.write("car");
            bw.newLine();

            //then add rows for the cars
            final File cars = new File(carDir);
           // for(final File car : cars.listFiles())
          //  {
                bw.write(imageToRowFromLinks(carDir,sections)+"1");
                bw.newLine();
                System.out.println("car " + carDir + " added");
          //  }
            //then add rows for the noncars
            final File noncars = new File(noncarDir);
         //   for(final File noncar : noncars.listFiles())
          //  {
                bw.write(imageToRowFromLinks(noncarDir,sections)+"0");
                bw.newLine();
                System.out.println("car " + noncarDir + " added");
         //   }
            bw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }





    @RequestMapping("/csv/read")
    public String readCsv() throws IOException {
        //String filename="cars.csv";


        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("csv/cars.csv");


        //FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        int count = 0;

        String line = br.readLine() + "\n";
        String outputText = line;
        while (count < 100){
            line = br.readLine();
            System.out.println("reading line");
            if(line != null)
                outputText += line + "\n";
            count++;
        }



        return outputText;
    }


    @RequestMapping("/csv/download")
    public String downloadAndReadCsv() throws IOException {
        String url="https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/cars.csv";
        String filename="cars.csv";
        int count = 0;

        try{
            URL download=new URL(url);
            System.out.println("Downloading csv file");
            ReadableByteChannel rbc= Channels.newChannel(download.openStream());
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
            System.out.println("Finished downloading csv file");
            fileOut.flush();
            fileOut.close();
            rbc.close();
        }catch(Exception e){ e.printStackTrace(); }


        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine() + "\n";
        String outputText = line;
        while (count < 100){
            line = br.readLine();
            System.out.println("reading line");
            if(line != null)
                outputText += line + "\n";
            count++;
        }


        return outputText;
    }
    /**
     * addPixelizedToFileName
     *
     * Modifies the filename of a fully qualified filepath, adding pixelized after the last /

     * @param filename the filename to be modified
     * @return that same filename but with the string pixelized added following the last /
     *

     */
    public static String addPixelizedToFileName(String filename)
    {
        //first pick up the portion of the filename dropping the url and add pixelized before it
        String pixeledFileName="pixelized"+filename.substring(filename.lastIndexOf("/")+1,filename.length());
        // then add back in the original url
        return filename.substring(0,filename.lastIndexOf("/")+1)+pixeledFileName;

    }

}
