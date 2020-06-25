/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GOERINGjavaHUB;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;
import javax.imageio.ImageIO;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import sun.misc.BASE64Encoder;

/*
Error status:
1: shutdown, bye
0:  OK
-1; not exist input file.
-2; unknnow cmd
-3; img convertion exception
-4; not used
*/

class MyError{
    public static String getError(int e){
        switch(e){
            case 0:     return "OK, not Error";
            case 1:     return "shutdown";
            case -1:    return "can't open input file";
            case -2:    return "unknow cmd";
            case -3:    return "img convertion error(exception)";
            case -4:    return "cmd format error";
            default:    return "not valid error (internal error)";
        }
    }
}

class ImgConverter {
 
    /**
     * Converts an image to another format
     *
     * @param inputImagePath Path of the source image
     * @param outputImagePath Path of the destination image
     * @param formatName the format to be converted to, one of: jpeg, png,
     * bmp, wbmp, and gif
     * @return true if successful, false otherwise
     * @throws IOException if errors occur during writing
     */
public static int convertFormat(String inImgPath, String outImgPath, 
        String formatName, Color color, int imageType) {
        File input = new File(inImgPath);
        if (!input.exists()){
            Log.log.severe(inImgPath+" could'nt be opened");
            return -1;
        }
        File output = new File(outImgPath);

        try { 
            BufferedImage img = ImageIO.read(input);
            BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(),
                    imageType);//   BufferedImage.TYPE_INT_RGB);
            //result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            //not transparent, PINK
            result.createGraphics().drawImage(img, 0, 0, color, null);
            ImageIO.write(result, formatName, output);
        } 
        catch (IOException ex) {
            Log.log.log(Level.SEVERE, (Supplier<String>) ex);
            return -3;
        }
        return 0;
    }

/*
public static int convert2BW(String inImgPath, String outImgPath) {
        File input = new File(inImgPath);
        if (!input.exists()){
            Log.log.severe(inImgPath+" could'nt be opened");
            return -1;
        }
        File output = new File(outImgPath);

        try { 
            BufferedImage img = ImageIO.read(input);
            BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(),
                                    BufferedImage.TYPE_BYTE_BINARY);
            //result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            //not transparent, PINK
            result.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
            ImageIO.write(result, "png", output);
        } 
        catch (IOException ex) {
            Log.log.log(Level.SEVERE, (Supplier<String>) ex);
            return -3;
        }
        return 0;
    }
*/

public static int encodeToString(String inImgPath, String outImgPath, String type) {
    PrintWriter out = null;
        try {
            File input = new File(inImgPath);
            if (!input.exists()){
                Log.log.severe(inImgPath+" could'nt be opened");
                return -1;
            }
            String imageString = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                BufferedImage img = ImageIO.read(input);
                ImageIO.write(img, type, bos);
                byte[] imageBytes = bos.toByteArray();
                
                BASE64Encoder encoder = new BASE64Encoder();
                imageString = encoder.encode(imageBytes);
                
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.log.log(Level.SEVERE, (Supplier<String>) e);
                return -3;
            }
            out = new PrintWriter(outImgPath);
            out.print(imageString);
            out.close();
            return 0;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImgConverter.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            out.close();
        }
    }
}

//Converts String to Color
class MyColor {
    public static Color getColor(String col) {
    switch (col.toLowerCase()) {
        case "black":       return Color.BLACK;
        case "blue":        return Color.BLUE;
        case "cyan":        return Color.CYAN;
        case "darkgray":    return Color.DARK_GRAY;
        case "gray":        return Color.GRAY;
        case "green":       return Color.GREEN;
        case "yellow":      return Color.YELLOW;
        case "lightgray":   return Color.LIGHT_GRAY;
        case "magneta":     return Color.MAGENTA;
        case "orange":      return Color.ORANGE;
        case "pink":        return Color.PINK;
        case "red":         return Color.RED;
        case "white":       return Color.WHITE;
        default:
            Log.log.warning("color "+col+" not found, default is PINK.");
            return Color.PINK;
        }
    }
}

class cmdParser{
    public String Cmd;
    public String iFile;
    public String oFile;
    public String Color;
    public boolean   Status;
    
    cmdParser(String cmd, String ifile, String ofile, String color, boolean st){
        this.Cmd=cmd;
        this.iFile=ifile;
        this.oFile=ofile;
        this.Color=color;
        this.Status=st;
    }
    
    public static int parse(String line){
        StringTokenizer st=new StringTokenizer(line,";");  
        
        if ( st.countTokens()==1
            && st.nextToken().toLowerCase().equals("shutdown") ){
            Log.log.info("shutdown cmd!");
            return 1;//shutdown
        }    
            
        if (st.countTokens()<3){
            Log.log.warning("cmd format error (need more parameters): "+line);
            return -4;//bad format
        }
        String[] token={"xxx", "input.jpg", "outut.jpg", "white"};
        int n=0;
        for(; st.hasMoreTokens(); ++n){
            token[n]=st.nextToken();
        }
        
        //System.out.println("cmd: "+token[0]);
        //System.out.println("ifile: "+token[1]);
        //System.out.println("ofile: "+token[2]);
        //System.out.println("color: "+token[3]);
        switch(token[0].toLowerCase()){
            case "png2jpg":
            case "jpg":
               java.awt.Color col=MyColor.getColor(token[3]);
               return ImgConverter.convertFormat(token[1], token[2], "jpg", 
                                col, BufferedImage.TYPE_INT_RGB);
               
            case "png2bw":
            case "bw":
               col=MyColor.getColor(token[3]);
               return ImgConverter.convertFormat(token[1], token[2], "png", 
                                col, BufferedImage.TYPE_BYTE_BINARY);
               
            case "png2txt64":
            case "txt":
                return ImgConverter.encodeToString(token[1], token[2], "png");
                        
            default://unknow cmd
                Log.log.warning("unknown cmd:\""+token[0]+"\"");
                return -2;//not recognized cmd
        }
    }
}

class myTCPserver{
    public static boolean serverLoop(int portNum){
        try{
            ServerSocket serverSocket = new ServerSocket(portNum);
            for(int i=0;;++i){
                try{
                    //System.out.print("("+i+")");
                    Socket socket = serverSocket.accept();
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line = reader.readLine();//reads a line of text
                    Log.log.fine("RECEIVED: "+line);
                    
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    long t0 = System.currentTimeMillis(); 
                    int st=cmdParser.parse(line);
                    if (st!=1){//shutdown
                        Log.log.fine("IMAGE Convertion Status: "+st);
                        Log.log.log(Level.INFO, MyError.getError(st));                    
                    }
                    //send response to client
                    if (st==0){
                        long tf = System.currentTimeMillis(); 
                        writer.println("OK("+(tf-t0)+"msecs)");
                        Log.log.fine("OK("+(tf-t0)+"msecs)");
                    }
                    else if (st==1){
                        writer.println("BYE");
                        socket.close();
                        Log.log.info("shutting down server, bye!");
                        return true;
                    }
                    else{    
                        writer.println("NOK: "+MyError.getError(st));
                        Log.log.severe("ERROR("+st+") "+MyError.getError(st));
                    }
                    socket.close();
                }
                catch (IOException ex) {
                    Log.log.severe((Supplier<String>) ex);
                }
            }
        }
        catch (IOException ex) {
            Log.log.log(Level.SEVERE, (Supplier<String>) ex);
            return false;
        }
    }
}

class Log{
    public static int portNum=6666;
    
    final public static Logger log=Logger.getLogger("Png2jpgServer");
    
    final public static Level getLevel(String level){
        switch(level.toLowerCase()){
            case "severe":
            case "highest":   return Level.SEVERE;
            case "warning":   return Level.WARNING;
            case "info":      return Level.INFO;
            case "config":    return Level.CONFIG;
            case "fine":      return Level.FINE;
            case "finer":     return Level.FINER;
            case "finest":    return Level.FINER;
            default:          return Level.FINE;
        }
    }
}

public class Png2jpgServer {
    /**
     * @param args the command line arguments
     */
    static String welcome="\n"+
"   ,ad8888ba,    ,ad8888ba,  88888888888 88888888ba  88 888b      88   ,ad8888ba,   \n" +
" d8\"'    `\"8b  d8\"'    `\"8b  88          88      \"8b 88 8888b     88  d8\"'    `\"8b  \n" +
"d8'           d8'        `8b 88          88      ,8P 88 88 `8b    88 d8'            \n" +
"88            88          88 88aaaaa     88aaaaaa8P' 88 88  `8b   88 88             \n" +
"88      88888 88          88 88\"\"\"\"\"     88\"\"\"\"88'   88 88   `8b  88 88      88888  \n" +
"Y8,        88 Y8,        ,8P 88          88    `8b   88 88    `8b 88 Y8,        88  \n" +
" Y8a.    .a88  Y8a.    .a8P  88          88     `8b  88 88     `8888  Y8a.    .a88  \n" +
"  `\"Y88888P\"    `\"Y8888Y\"'   88888888888 88      `8b 88 88      `888   `\"Y88888P\"   \n" +
"This JAVA Server service is developed by Guillermo Tomasini Redondas\n"+
"This is part of i4Print, licenced product of GOERING GmbH Germany\n"+
"For more information please visit: www.goering.de\n" ;
    
    public static void main(String[] args) {
        Level level=Level.ALL;
        Log.log.setLevel(level);
        if (args.length>=2) {
            level = Log.getLevel(args[1]);
            Log.log.setLevel(level);
        }        
        else{
            System.out.println("Log level not provided (second arg)");
            System.out.println("\t(severe, warning, info, config, fine, finer, finest, all)");
            Log.log.info("not log_level provided (not second argument)");
        }
        int portNum = 6666;
        if (args.length>=1) {
            try{
                portNum=Integer.parseInt(args[0]);
            }catch(NumberFormatException ex){
                Log.log.info("portNumber(first argument would be an integer)");
            }
        }        
        else
            Log.log.info("not port provided (not first argument)");
        try {
            FileHandler handler = new FileHandler("log_"+portNum+".log", false);
            Log.log.addHandler(handler);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(level);
            //Log.log.setUseParentHandlers(false);
            //Logger.getLogger("").getHandlers()[0].setLevel( Level.OFF );
            for(Handler h : java.util.logging.Logger.getLogger("").getHandlers())    
                h.setLevel(level);

            Log.log.info(welcome);
            Log.log.config("simple formatter");
            Log.log.fine("Welcome (from Fine Level)!");
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(Png2jpgServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        Log.log.info("listening on port "+portNum);
        myTCPserver.serverLoop(portNum);
    }
}
