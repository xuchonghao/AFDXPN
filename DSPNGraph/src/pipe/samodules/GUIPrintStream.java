package pipe.samodules;
    import java.io.OutputStream;  
import java.io.PrintStream;  
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;  
import javax.swing.text.JTextComponent;  
      

      
    public class GUIPrintStream extends PrintStream {  
        //private JTextComponent component;  
        private JLabel component; 
        private StringBuffer sb = new StringBuffer();  
        public GUIPrintStream(OutputStream out, JLabel component) {  
            super(out);  
            this.component = component;  
        }  
         
     
      
        @Override  
        public void write(byte[] buf, int off, int len) {  
            final String message = new String(buf, off, len);             
            SwingUtilities.invokeLater(new Runnable() {  
                public void run() {  
                    sb.append(message);  
                    component.setText(sb.toString()); 
                    
                }  
            });  
        }  
    }  