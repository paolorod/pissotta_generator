/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pissottagenerator;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;


/**
 *
 * @author paolo
 */
public class Main {
    private static final String OPT_NUM_SIMBOLS_HORIZONTAL = "h";
    private static final String OPT_NUM_SIMBOLS_VERTICAL = "v";
    private static final String OPT_LIBRARY_DIR = "l";
    private static final String OPT_NUM_SCHEDE = "n";
    private static final String OPT_OUTPUT_DIR = "o";
    
    private static final int DEFAULT_NUM_SIMBOLS_VERTICAL = 2;
    private static final int DEFAULT_NUM_SIMBOLS_HORIZONTAL = 5;
    private static final int DEFAULT_WIDTH= 1020;
    private static final int DEFAULT_HEIGHT= 420;
    private static int NUMBER_OF_PISSOTTA_PER_PAGE = 3;
    private static int DEFAULT_PAGE_MARGIN_UR = 10;
    
    
    
      /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws DocumentException {
        GnuParser parser = new GnuParser();
        try {
            CommandLine cli = parser.parse(getOptions(), args);
            ImageLibrary library = new ImageLibrary(cli.getOptionValue(OPT_LIBRARY_DIR));
            
            int num = Integer.parseInt(cli.getOptionValue(OPT_NUM_SCHEDE));
           
            int horSimbols = manageIntDefault(cli,OPT_NUM_SIMBOLS_HORIZONTAL,DEFAULT_NUM_SIMBOLS_HORIZONTAL);
            int verSimbols = manageIntDefault(cli,OPT_NUM_SIMBOLS_VERTICAL,DEFAULT_NUM_SIMBOLS_VERTICAL);

            
            File output = new File(cli.getOptionValue(OPT_OUTPUT_DIR));
            if(!output.isDirectory()) throw new IllegalArgumentException("Directory non valida per l'output");
            
            PissottaGenerator generator = new PissottaGenerator(library, verSimbols, horSimbols, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
            System.out.println("Library Size: "+library.getSize());
            System.out.println("Sequence Size: "+(horSimbols*verSimbols));
            
            System.out.println("Setting up rectangles for pdf");
            Rectangle pagesize = PageSize.A4;
            float width_ur = pagesize.getWidth()-2*DEFAULT_PAGE_MARGIN_UR;
            float height_ur = width_ur*((float)DEFAULT_HEIGHT/(float)DEFAULT_WIDTH);
            float spacer_ur =  (pagesize.getHeight()-2*DEFAULT_PAGE_MARGIN_UR-NUMBER_OF_PISSOTTA_PER_PAGE*height_ur)/(1+NUMBER_OF_PISSOTTA_PER_PAGE);
            Rectangle pissottaRect = new Rectangle(width_ur,height_ur);
            Rectangle spacerRect = new Rectangle(width_ur,spacer_ur);
            
            System.out.println("Preparing spacer"); 
            Image spacer = Image.getInstance(getSpacerImage(DEFAULT_WIDTH,Math.round(DEFAULT_WIDTH*spacer_ur/width_ur)),Color.white);
            spacer.scaleToFit(spacerRect);
            
            //new Rectangle(DEFAULT_WIDTH+20,DEFAULT_HEIGHT*4+DEFAULT_PAGE_MARGIN*2+(DEFAULT_SPACER*NUMBER_OF_PISSOTTA_PER_PAGE));
            
            
            System.out.println("Starting writer");
            Document doc = new Document(pagesize, DEFAULT_PAGE_MARGIN_UR, DEFAULT_PAGE_MARGIN_UR, DEFAULT_PAGE_MARGIN_UR, DEFAULT_PAGE_MARGIN_UR);
            PdfWriter.getInstance(doc, new FileOutputStream(output.getPath()+"/pissotta.pdf"));
            doc.open();

            System.out.println("Starting to generate");
            
            String basename = "pissotta";
            for(int i=0; i<num; i++) {
                doc.add(spacer);
                Image generatedImage = Image.getInstance(generator.generate(),Color.white);
                generatedImage.scaleToFit(pissottaRect);
                doc.add(generatedImage);
               if((i+1)%NUMBER_OF_PISSOTTA_PER_PAGE == 0) {
                doc.newPage();
               }
            }
            
            doc.close();
            
            
        } catch (ParseException ex) {
            printHelp();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            printHelp();
        } 
        
        
    }
    
    
    public static Options getOptions() {
        
        Options opt = new Options();
        opt.addOption(OptionBuilder.isRequired().hasArg().withArgName("path").withDescription("Output Directory").create(OPT_OUTPUT_DIR));
        opt.addOption(OptionBuilder.isRequired().hasArg().withArgName("path").withDescription("Libreria di Simboli da usare").create(OPT_LIBRARY_DIR));
        opt.addOption(OptionBuilder.hasArg().withArgName("num").withDescription("Numero simboli orizzontali").create(OPT_NUM_SIMBOLS_HORIZONTAL));
        opt.addOption(OptionBuilder.hasArg().withArgName("num").withDescription("Numero simboli verticali").create(OPT_NUM_SIMBOLS_VERTICAL));
        opt.addOption(OptionBuilder.hasArg().withArgName("num").withDescription("Numero schede").create(OPT_NUM_SCHEDE));
        
        return opt;
    }
    
    private static java.awt.Image getSpacerImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth(), image.getHeight()); 
        return image;
    }
    

    private static int manageIntDefault(CommandLine cli, String option, int defaultValue) {
        if(cli.hasOption(option)) {
            return Integer.parseInt(cli.getOptionValue(option));
        }
        return defaultValue;
    }
    
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar pissottaGenerator.jar", getOptions());
    }
}
