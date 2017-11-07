package wasdev.sample.rest;



import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@ApplicationPath("api")
@Path("/csv")
public class CsvDataAPI {

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
     * Used for testing. Creates cars.csv from /cars and /noncars with a 10 x 10 grid


     */
    @GET
    @Path("/")
    @Produces({"application/json"})
    public static void main(String args[])
    {

        createCSVFromDirectories("C:\\images\\cars","C:\\images\\noncars","C:\\csv\\cars.csv",10);
        System.out.println("done");
    }

}
