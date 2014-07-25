/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.Scanner;

/**
 * Aimed at writing writing a fucntion the converts the class of list of fucntion. 
 * @author FrontGate
 */
public class BVHFormat {
    
    private int rootOffsetX;
    private int rootOffsetY;
    private int rootOffsetZ;
    
    private int neckOffsetX;
    private int neckOffsetY;
    private int neckOffsetZ;
    
    private int endSiteOffsetX;
    private int endSiteOffsetY;
    private int endSiteOffsetZ;
   //Direcotry where the landmarks are contained. 
    
   /**
    * For letting the user decide which file from the direcotry to choose for Neutral Pose of the face. 
    * If the value of the normalPoseChoice is -1 then the user can set a file. 
    * */
    
    //For letting the user choose the Frame time;
    private double frameTime;  
    
    private double scalingFactor;
    private int frameNumbers;
    private int normalPoseChoice;// Is this information necessary to make it run. 
    // How am i going to set the normalPoseChoice; From the constructor or is it necessary to make that change. 
    // Or the scaling facotr acctordingly. i.e How much time does it take to make that things run. 
    
    private String strLine;
    private StringBuilder fileContent = new StringBuilder();
    
    // This is the file locations for the corresponding files; Thus consider the file here. 
    private File baseTemplateFile;
    private File saveToFile;
    private File normalPoseFileChoice=null;
    private File ci2cvFilesDirectory;
    
    public BVHFormat(File saveLocation, File templateLocation, File landmarkLocation,File normalPoseFileLocation, double frameTime, double scaling)
    {
        baseTemplateFile=templateLocation;
        saveToFile= saveLocation;
        ci2cvFilesDirectory= landmarkLocation;
        normalPoseFileChoice=normalPoseFileLocation;
        setScalingFactor(scaling);
       
        setRootOffset(0,0,0);
        setNeckOffset(0,0,0);
        setEndSiteOffset(0,0,10);
        setFrameTime(frameTime);
        
                
    }
    private void setScalingFactor(double scalingFactor)
    {
        this.scalingFactor=scalingFactor;
    }
    private void setRootOffset(int x, int y, int z)
    {
        rootOffsetX=x;
        rootOffsetY=y;
        rootOffsetZ=z;
    }
    private void setNeckOffset(int x, int y, int z)
    {
        neckOffsetX=x;
        neckOffsetY=y;
        neckOffsetZ=z;
    }
    private void setEndSiteOffset(int x, int y, int z)
    {
        endSiteOffsetX=x;
        endSiteOffsetY=y;
        endSiteOffsetZ=z;
    }
    private void setFrameNumber(int x)
    {
       frameNumbers= x;
    }
    private void setCi2CVFilesDirectory(File file)
    {
        ci2cvFilesDirectory= file;
    }
    private void setNormalPoseChoice(int choice)
    {
        normalPoseChoice=choice;
    }
    private void setNormalPoseFileChoice(File file)
    {
        normalPoseFileChoice=file;
    }
    private void setFrameTime(double frameTime)
    {
        this.frameTime=frameTime;
    }
    private void setSaveToFile(File file)
    {
        saveToFile= file;
    }
    private void setTemplateFile(File file)
    {
        baseTemplateFile=file;
    }
    /**
     * CreateBVHFIle is used to create the .BVH file from by relpalcing approprita locals in the file. 
     * @return
     * True or false to indicate the sucess of the creation of the file. 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public boolean createBVHFile() throws FileNotFoundException, IOException
    {
            File[] listFiles=getFileList();
            boolean success= false;
            FileInputStream fstream = new FileInputStream(baseTemplateFile);// Load the basic template file
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));  //Load it up for the reader
            //Here the trick is i am using the basic offset for the base file and thus
            //thus whenever in the search we are using the file we get to replace OFFSET *** with the the offset as 
            //obtained from the normalPoseFileChoice. 
            String[] offset=getPoseString(normalPoseFileChoice);
            
            int n=0;
            
            while ((strLine = br.readLine()) != null)
               
            {                  
                if(strLine.contains("*ROOT"))
                {
                    //System.out.println("Entered ROOT");
                    strLine="\tOFFSET "+rootOffsetX+" "+rootOffsetY+" "+rootOffsetZ;
                }
                else if(strLine.contains("*NECK"))
                {
                    strLine="\t\tOFFSET "+neckOffsetX+" "+neckOffsetY+" "+neckOffsetZ;
                //    System.out.println("Entered NECK");
                }
                else if(strLine.contains("*ENDSITE"))
                {
                 //   System.out.println("Entered ENDSITE");
                    strLine="\t\t\tOFFSET "+endSiteOffsetX+" "+endSiteOffsetY+" "+endSiteOffsetZ;
                    
                }
                else if (strLine.contains("***"))
                {
                 //   System.out.println("Entered OFFSET ***");
                    strLine="\t\t\tOFFSET "+offset[n];
                    // Here is the where the normal values are set; 
                            n++;
                }
                else if (strLine.contains("*FRAMENUMBER"))
                {
                   // System.out.println("Entered FRAMENUMBER");
                    //set the number of files in the directory; 
                    strLine="FRAMES: " + Integer.toString(frameNumbers);
                    
                }
                else if (strLine.contains("*FRAMETIME"))
                { //System.out.println("ENTERS FRAMES");
                    strLine="FRAMES TIME: "+ frameTime;
                }
                else if (strLine.contains("*JOINT"))
                {
                    //System.out.println("Entered Joint");
                    // For all the files in the directory; Iterate thru all the files and append it to the file.
                    // Break the file to jump out of it as the appending file must 
                    
                    for (File file:listFiles)
                    {
                         fileContent.append("0 0 0 0 0 0 ");
                       String[] landmarks= getPoseString(file);
                         
                       for (int i=0; i<66;i++)
                       {// Dont' forget to add the normal pose location for the head locations
                          // System.out.println(landmarks[i]);
                           fileContent.append(landmarks[i]);
                           fileContent.append(" ");
                                   
                       }
                       fileContent.append("\n");
                    }
                    success= true; 
                    break;
                }
                
                fileContent.append(strLine);
                fileContent.append("\n");
            }
        if (success)
        {
           
           FileWriter fstreamWrite= new FileWriter(saveToFile);
           BufferedWriter out= new BufferedWriter(fstreamWrite);
           out.write(fileContent.toString());
           out.close();
           br.close();
        }
        
        return success;
    }
    /**
     * 
     * @param file
     * @return
     * Used to get Arrays of String for both offset from the root and the motion data from the a given file. As it assumes it's a 
     * pts file the; it performs it's tasks by skipping a couple of lines and then taking the lines. 
     * 
     * Generally it assumes it's a .pts file that is obtained from the that of the ci2cv file. 
     * @throws FileNotFoundException 
     * Throws the execption when the it's incapable of reading from the the specified file the required parameters. 
     */
    private String[] getPoseString(File file) throws FileNotFoundException
    {
        String[] normalPose=new String[66];
        
            Scanner scanner= new Scanner(file);
            
            String in=scanner.nextLine();
            scanner.nextLine();
            
            for (int i=0;i<66;i++)
            {
                String newLine=scanner.nextLine();
                String tokens[] = newLine.split(" ");
                if(tokens.length>0)
                {
                    // Before all of these information disclosed below. I may have got all the BVH thing wrong. Thus 
                    // Spend quite a few time doing some understanding of the faceshift BVH file and at least using the 
                    // basic hierachy for the file. Also how the pose is organized. It seems elegant; Do something about it. 
                    
                    // Also check how the pose is direct shift from the base of the neck and thus gives a corresponding 
                    // overall look and feel for the object. 
                    
                    // It's also essential to remember to include the concept of how the Blendshapes can help in the rigging
                    
                    // THus study the rigging for yourself thus to be able to say specific things on the concept of it needing to be included; 
                    
                    // THus ask sami whether it's a needed requirement to develop; Since the concept of normalizing the dimensions applys for 
                    // to determine the and maximizing the facts;
                    
                    // In that case also consider the fact that the image size has an effect so Check that out also. 
                    
                    // So these are the ideas on top of my mind floating. 
                        /**
                         * Here place the things and ideas after results are posted. 
                    */
                    
                    
                    
                    // Is it the case that i need to add or multiply to give the effect more 
                    //of a natural look. Try it both ways and see the effect both ways. 
                    // Make the case for both ways; How?
                    // Multiplication is like scalling but that has the effect of making it a larger skeleton; But doesn't that show to be
                    // an indication 
                    
                    // But also check the way that how one can make the video appropriate thus creating a dynamic file to test the file by.
                    // Thus check the following difference 
                        // * Create a video in which the movements are distinicitivly different. 
                        // ** Scale the factors and see the difference. 
                        // *** Clearly note what are the difference in the two files called 
                    
                    //How can a person add using opencv rotational data using the orientation of nose and eyes to detect rotational data.
                    // Are there other means of doing these things accordingly. 
                    
                                        
                    // ALso consider selective scaling; As it seems that the effect of the head movement is not that much pronouced thust try
                    // to pronounce the movement of the head.
                    
                    //Or should i use the two dimensional results from the data. Check by wirting a program for the file. 
                   
                   double x=Double.parseDouble(tokens[0])*scalingFactor;// Replace them with fancy function to do. 
                   double y=Double.parseDouble(tokens[1])*scalingFactor;// Now what kind mathematically fucntion will do these must be read. 
                   double z=Double.parseDouble(tokens[2])*scalingFactor;// All these is dependent on the predefined assumptions made earlier. 
                   
                   
                   
                    // Here is where the modification for the files must occur. Like for different values to scale. 
                    normalPose[i]= Double.toString(x)+" "+Double.toString(y)+" "+Double.toString(z);
                    
                }
               
            }// Here is the where the normalizing factor must take place. 
          scanner.close();
         return normalPose;
    }
    /**
     * These method used to get all the files from the direcotry. 
     * 
     * @return returns An array of Files in the directory. It assumes the files are all .pts file. 
     */
    
    public File[] getFileList()
    {
      File[] listFiles=ci2cvFilesDirectory.listFiles();
      frameNumbers=listFiles.length;
      
      return listFiles;
    }
 
  
    
    
}
