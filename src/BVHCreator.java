/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/**
 *
 * @author FrontGate
 */
public class BVHCreator {
    public static File getCi2CvFramesDirectory()
    {
        File file= null;
        JFileChooser jFileChooser= new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result= jFileChooser.showOpenDialog(new javax.swing.JFrame());
        
        if (result == JFileChooser.CANCEL_OPTION)
        {return file;}
        file=jFileChooser.getSelectedFile();
        
        if (file==null || (file.getName().equalsIgnoreCase("")))
        {
            JOptionPane.showMessageDialog(jFileChooser, "Invalid Selection", "Invalid Directory", JOptionPane.ERROR_MESSAGE);
            return file;
        }
        return file;
    }
    public static File getSaveToFileLocation()
    {
        File file= null;
        JFileChooser jFileChooser= new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result= jFileChooser.showSaveDialog(new javax.swing.JFrame());
        if (result== JFileChooser.CANCEL_OPTION)
        {
            return file;
        }
        //file=jFileChooser.getSelectedFile();
        
        file= new File(jFileChooser.getSelectedFile().toString()+".bvh");
        if (file==null || (file.getName().equalsIgnoreCase("")))
        {
            JOptionPane.showMessageDialog(jFileChooser, "Invalid Selection", "Invalid Directory", JOptionPane.ERROR_MESSAGE);
            return file;
        }
        return file;
    }
    public static File setOpenFile()
    {
        File file= null;
        JFileChooser jFileChooser= new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result= jFileChooser.showOpenDialog(new javax.swing.JFrame());
        
        if (result == JFileChooser.CANCEL_OPTION)
        {return file;}
        file=jFileChooser.getSelectedFile();
        
        
        if (file==null || (file.getName().equalsIgnoreCase("")))
        {
            JOptionPane.showMessageDialog(jFileChooser, "Invalid Selection", "Invalid Directory", JOptionPane.ERROR_MESSAGE);
            return file;
        }
        return file;
    }        
    
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        
      File saveLocation=getSaveToFileLocation();
      File templateFileLocation=new File("template.bvh");// set the template to convert the file. 
      File landmarkLocation=getCi2CvFramesDirectory();
      File normalPoseLandmarkFileLocation=setOpenFile();
       
       // Here the frame time is obviously erroneous for most cases as it depends on how the video was made; And that would be resolved further down the line
	// Scaling factor is also not that much useful currently and that will chage as geometric computations are needed
       BVHFormat bvhformat= new BVHFormat(saveLocation, templateFileLocation, landmarkLocation, normalPoseLandmarkFileLocation,0.033333,1);
       
       boolean success= bvhformat.createBVHFile();
       //Here make sure that the save file is created to be passed on it. 
       if(success)
       {
           System.out.printf("File Created Check it out %s", saveLocation.toString());
           System.exit(0);
       }
       else 
       {
           System.out.println("File Not Created");
           System.exit(1);
       }
        
    }
  
}
