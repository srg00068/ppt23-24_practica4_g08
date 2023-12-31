package practica_4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Protocolos de Transporte
 * Grado en Ingenier�a Telem�tica 
 * Dpto. Ingen�er�a de Telecomunicaci�n Univerisdad de Ja�n
 *
 *******************************************************
 * Pr�ctica 4. 
 * Fichero: HttpConnection.java
 * Versi�n: 1.0 
 * Curso: 2023/2024
 * Descripci�n: Clase sencilla de atenci�n al protocolo HTTP/1.1 
 * Autor: Juan Carlos Cuevas Mart�nez
 *
 ******************************************************
 * Alumno 1: Sergio Real González  
 * Alumno 2: Sebastian Zamora Molina 
 *
 ******************************************************/
public class HttpConnection implements Runnable {

	Socket socket = null;

	public HttpConnection(Socket s) {
		socket = s;
	}

	@Override
	public void run() {
		DataOutputStream dos = null;
		try {
			System.out.println("Starting new HTTP connection with " + socket.getInetAddress().toString());
			dos = new DataOutputStream(socket.getOutputStream());
			//dos.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
			//dos.flush();
			BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line = bis.readLine(); //Get/ruta/HTTP1.1
			String partes[]=line.split(" ");
			if(partes.length==3){
                            System.out.println("Cabecera host:[" +partes[1]+"]: ");
				if (partes[0].compareToIgnoreCase("get")==0){

					while (!(line=bis.readLine()).equals("") && line!=null) {
						System.out.println("Le�do["+line.length()+"]: "+line);
						//dos.write(("ECO " + line + "\r\n").getBytes());
						//dos.flush();
					}

					//Abrir archivo comparando la segunda posicion de la cadena, y en el catch error 404


                                        byte[] data=readFile(partes[1]);
                                        
                                        if(data==null){
                                            
                                        }else{
                                        
					dos.write("HTTP/1.1 200 Ok\r\n".getBytes());
                                        dos.write(("Content-Type:"+GetContenType(partes[1])+"\r\n").getBytes());
                                        dos.write(("Content-Length:"+data.length+"\r\n").getBytes());
                                        dos.write("\r\n".getBytes()); //Fin de cabeceras;
					dos.flush();
					dos.write(data);
                                        }
				}else{
					dos.write("HTTP/1.1 405 Metod is not Allowed\r\n\r\n".getBytes());
					dos.flush();
				}

			}else{
				dos.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes()); //el segundo \r\n es fin de cabeceras
				dos.flush();
			}
                }catch(FileNotFoundException ex){
                                try{
                                dos.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes()); //el segundo \r\n es fin de cabeceras
				dos.flush();
                                } catch (IOException ex1){
                                   System.out.println("error"); 
                                }
                                  
                                
                //comprobar si el metodo utilizado es get. si no es get error 405
			
                }catch (IOException ex2) {
			try{
                                dos.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes()); //el segundo \r\n es fin de cabeceras
				dos.flush();
                                } catch (IOException ex1){
                                   System.out.println("error"); 
                                }
                                  
		} finally {
			try {
				dos.close();
				socket.close();
			} catch (IOException ex) {
				Logger.getLogger(HttpConnection.class.getName()).log(Level.SEVERE, null, ex);
			}
		

	}
        
        }

        
        protected  byte[] readFile(String path) throws FileNotFoundException, IOException{ 
            File f=new File ("."+path);
            FileInputStream fis=new FileInputStream(f);   
            byte [] datos;
            datos = new byte[(int)f.length()];
            fis.read(datos);
            System.out.println("Cabecera host:"+ socket.getLocalAddress());
                return (datos);
            
            //Ahora lee archivos
        }
        
        protected String GetContenType(String path){
            if(path.endsWith(".html")||path.endsWith(".htm")){
                return "text/html";
            }
            else if(path.endsWith(".jpg")||path.endsWith(".jpeg")){
                return "image/jpg";
            }
            else if(path.endsWith(".css")){
                return "text/css";
            }else{
               
                String [] n=path.split(".");
                if(n.length>=2){
                 return "application/"+n[n.length-1];   
                }else{
                    return "ns";
                }
                
            }
        }
}

