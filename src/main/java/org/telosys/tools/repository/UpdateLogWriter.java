/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.repository ;

/**
 * @author Laurent GUERIN
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UpdateLogWriter
{
    private final static int         LF              = 1;

    private final static int         CRLF            = 2;

    private int                      endOfLine     = LF; // for future use ?
	
	//private FileOutputStream _fos = null ;
	private OutputStream outputStream = null ;
	
    //-----------------------------------------------------------------------------
    //--- CONSTRUCTORS
    //-----------------------------------------------------------------------------
    /**
     * Constructor for a file logger
     * @param file
     */
    public UpdateLogWriter(File file) {
        if (file != null) {
            try {
                outputStream = new FileOutputStream(file);
            } 
            catch (FileNotFoundException ex) {
            	// Cannot create file
            	throw new RuntimeException("UpdateLogWriter : Cannot create file '" + file.getAbsolutePath() + "'");
            }
        }
        else {
        	throw new RuntimeException("UpdateLogWriter constructor : file parameter is null");
        }
    }
    
    /**
     * Constructor for logging "in memory"
     * @param byteArrayOutputStream
     */
    public UpdateLogWriter(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream != null) {
        	outputStream = byteArrayOutputStream ;
        }
        else {
        	throw new RuntimeException("UpdateLogWriter constructor : file parameter is null");
        }
    }
    
    //-----------------------------------------------------------------------------
    synchronized public void println( String msg )
    {
    	System.out.println("LogWritter.println (" + msg + ")");
        if (outputStream != null) {
            try {
                outputStream.write(msg.getBytes());
                if (endOfLine == CRLF)  {
                	outputStream.write('\r');
                	outputStream.write('\n');
                }
                else {
                	outputStream.write('\n');
                }
                outputStream.flush();
            } 
            catch (IOException ex) {
            	throw new RuntimeException("UpdateLogWriter : cannot write (IOException)");
            }
        }
    }
    
    //-----------------------------------------------------------------------------
    synchronized public void close()
    {
        if (outputStream != null) {
        	try {
				outputStream.close();
			} 
        	catch (IOException e) {
				throw new RuntimeException("UpdateLogWriter : cannot close (IOException)");
			}
        }
    }
}