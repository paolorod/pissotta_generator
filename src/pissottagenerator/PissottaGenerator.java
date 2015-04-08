/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pissottagenerator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author paolo
 */
public class PissottaGenerator {
    
    
    
    private final int spanExternal = 10;
    private final int spanBetweenImage = 10;
    private final int spanAroundImage = 10;
    private final int borderAroundImage = 3;
    
    
    Random random = new Random();
    
    private final ImageLibrary library;
    private final int numSimbolsVertical;
    private final int numSimbolsHorizontal;
    private final int outputWidth;
    private final int outputHeight;
    private final int simbolWidth;
    private final int simbolHeight;
    private final SequenceGenerator sequenceGenerator;
    private final int remainderWidth;
    private final int remainderHeight;
    
    public PissottaGenerator(ImageLibrary library, int numSimbolsVertical, int numSimbolsHorizontal, int outputWidth, int outputHeight) {
        
        if(numSimbolsHorizontal<0 || numSimbolsVertical<0) throw new IllegalArgumentException("Il numero di simboli deve essere positivo");
        if(outputWidth<=0 || outputWidth<=0) throw new IllegalArgumentException("Le dimensioni di output devono essere positive");
        if(library == null) throw new IllegalArgumentException("La library deve essere definita");
        
        
        this.numSimbolsVertical = numSimbolsVertical;
        this.numSimbolsHorizontal = numSimbolsHorizontal;
        
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        
        int availableSimbolWidth = outputWidth-2*spanExternal- (numSimbolsHorizontal-1)*spanAroundImage;
        int availableSimbolHeight = outputHeight-2*spanExternal- (numSimbolsVertical-1)*spanAroundImage;
        
        this.simbolWidth =  availableSimbolWidth/numSimbolsHorizontal;
        this.simbolHeight = availableSimbolHeight/numSimbolsVertical;
        
        this.remainderWidth = availableSimbolWidth%numSimbolsHorizontal;
        this.remainderHeight = availableSimbolHeight%numSimbolsVertical;
    
        this.sequenceGenerator = new UniqueSequenceGenerator(library.getSize());
        this.library =library.resize(simbolWidth-26, simbolHeight-26).addBorder(spanAroundImage, Color.white).addBorder(borderAroundImage, Color.BLACK);
    
        System.out.println("simbol width:"+simbolWidth+" height:"+simbolHeight);
        System.out.println("remainder width:"+ remainderWidth +" height:"+remainderHeight);
        
    }
    
    /**
     * Produce una cartella in output
     * @return 
     */
    public BufferedImage generate() {
        Integer[] seq = sequenceGenerator.generateSequence(numSimbolsHorizontal*numSimbolsVertical);
        BufferedImage image = getBaseImage();
        Graphics g = image.getGraphics();
        for(int i=0; i<seq.length; i++) {
            int y = remainderHeight/2 + spanExternal + (i/numSimbolsHorizontal)*(simbolHeight+spanAroundImage);
            int x = remainderWidth/2  + spanExternal + (i%numSimbolsHorizontal)*(simbolWidth+spanAroundImage); 
            //System.out.println("x:"+x+" ,y:"+y);
            g.drawImage(library.getImageAt(seq[i]), x, y, null);
            
        }
        return image;        
    }


    private BufferedImage getBaseImage() {
        BufferedImage image = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());  
        g.dispose();
        return image;
    }
    
    
   

    
    
}
