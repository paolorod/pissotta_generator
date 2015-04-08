/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pissottagenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 *
 * @author paolo
 */
public class ImageLibrary {


    
    private final FileFilter imageFilter = new ImageFilter();
    LinkedList<ImageFile> library = new LinkedList<ImageFile>();

    
    
    public ImageLibrary(String location) throws IOException {
        File dir = new File(location);
        if(!dir.isDirectory()) throw new IllegalArgumentException("Serve una cartella per continuare");  
        File[] listFiles = dir.listFiles(imageFilter);
        if(listFiles.length == 0 ) throw new IllegalArgumentException("Nessun file valido trovato");
        ;
        for(File f: listFiles) {
            ImageFile imfile = new ImageFile(ImageIO.read(f), f);
            library.add(imfile);
        }
        Collections.sort(library);        
    }
    
    private ImageLibrary(LinkedList<ImageFile> library) {
        this.library= library;
    }

      
    
    public Image getImageAt(int index) {
        return library.get(index).getImage();
    };
    
    public String getImagePathAt(int index) {
        return library.get(index).getFile().getPath();
    }
     
    public int getSize() {
        return library.size();
    }
   


    private static class ImageFilter implements FileFilter {
        
        private String imageNameRegex =".*png|.*jpg|.*jpeg|.*gif|.*JPG|.*PNG";

        public ImageFilter() {
        }

        @Override
        public boolean accept(File file) {
            return !file.isDirectory() && file.getName().matches(imageNameRegex);
        }
    }
    
    public ImageLibrary resize(int width, int height) {
        LinkedList<ImageFile> newLibrary = new LinkedList<ImageFile>();
        for(ImageFile i:library) {        
            Image j = simbolToSize(i.getImage(), width, height);
            newLibrary.add(new ImageFile(j, i.getFile()));
        }
        return new ImageLibrary(newLibrary);
    }
    
    public ImageLibrary addBorder(int borderSize, Color color) {
         LinkedList<ImageFile> newLibrary = new LinkedList<ImageFile>();
        for(ImageFile i:library) {
            Image j = simbolBorder(i.getImage(), borderSize, color);
            newLibrary.add(new ImageFile(j, i.getFile()));
        }
         return new ImageLibrary(newLibrary);
    }
    
    
    
       /**
     * Riduce una immagine alla dimensione richiesta
     * @param simbol
     * @return 
     */
    private Image simbolToSize(Image simbol, int width, int height) {
      
        if(simbol.getWidth(null) > width) {
            simbol = simbol.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
        }
        if(simbol.getHeight(null) > height) {
            simbol = simbol.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
        }
        
        int scaledWidth = simbol.getWidth(null);
        int scaledHeight = simbol.getHeight(null);
          
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());  
        g.drawImage(simbol, (width-scaledWidth)/2,(height-scaledHeight)/2, null);
        g.dispose();
      
        return image;
    }
    
    
     private Image simbolBorder(Image simbol, int borderSize, Color color) {
         
         int width = simbol.getWidth(null);
         int height = simbol.getHeight(null);
         
        BufferedImage image = new BufferedImage(width+2*borderSize, height+2*borderSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
       
        g.fillRect(0, 0, image.getWidth(), image.getHeight());  
        g.drawImage(simbol, borderSize, borderSize, null);
        g.dispose();     
        return image;
    }
     
   private class  ImageFile implements Comparable{

        public ImageFile(Image image, File file) {
            this.image = image;
            this.file = file;
        }
            private Image image;
            private File file;

        public File getFile() {
            return file;
        }

        public Image getImage() {
            return image;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImageFile other = (ImageFile) obj;
            if (this.image != other.image && (this.image == null || !this.image.equals(other.image))) {
                return false;
            }
            if (this.file != other.file && (this.file == null || !this.file.equals(other.file))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + (this.image != null ? this.image.hashCode() : 0);
            hash = 59 * hash + (this.file != null ? this.file.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "ImageFile{" + "files=" + file.getAbsolutePath() + '}';
        }

        /**
         * Order based on File name
         * @param t
         * @return 
         */
        @Override
        public int compareTo(Object t) {
            if(!(t instanceof ImageFile)) {
                 return -1000;
            } 
            ImageFile other = (ImageFile)t;
            
            return this.getFile().toString().compareTo(other.getFile().toString());
            
        }
            
        
       
       
    }
    
}
