/*
 * Servlet de Encuesta - Martín Arnesi - AUS 2007
 * Fecha= 23/10/2007
 * Software= Notepad++ - Apache Tomcat 5.5 - MySql 5.0
 * Base de datos= ip: 127.0.0.1 - bdd = servlet - User&Pass= root table=encuesta
*/

//Importo Clases
import java.io.*;
import java.text.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class servEncuesta extends HttpServlet {private Connection conexion; private Statement statement;

   
	//establecer conexión a la base de datos y crear instrucción de SQL en metodo Init
	public void init( ServletConfig config ) throws ServletException{
      
	   // intentar conexión a la base de datos y crear objeto Statement
	   try 
	   {
        Class.forName("com.mysql.jdbc.Driver");
		Connection conexion=DriverManager.getConnection("jdbc:mysql://127.0.0.1/servlet?user=root&password=root");
		statement=conexion.createStatement();
	   }
	   catch ( Exception excepcion ) 
	   {
         excepcion.printStackTrace();
         throw new UnavailableException( excepcion.getMessage() );
	   }

	}

   
	// procesar respuesta de la encuesta
	protected void doPost( HttpServletRequest peticion,HttpServletResponse respuesta )throws ServletException, IOException{
      
	// establecer respuesta para el cliente
    respuesta.setContentType( "text/html" ); 
    PrintWriter salida = respuesta.getWriter();
    DecimalFormat dosDigitos = new DecimalFormat( "0.00" );

    // empezar documento de XHTML
    salida.println( "<?xml version = \"1.0\"?>" );
    
    salida.println( "<!DOCTYPE html PUBLIC \"-//W3C//DTD " +
         "XHTML 1.0 Strict//EN\" \"http://www.w3.org" +
         "/TR/xhtml1/DTD/xhtml1-strict.dtd\">" ); 
    
    salida.println("<html xmlns = \"http://www.w3.org/1999/xhtml\">" );
    
    // sección de encabezado del documento
    salida.println( "<head>" );  
    // leer respuesta actual de la encuesta enviada por ServletEncuesta.html
    String so = peticion.getParameter("so"); //so toma el string con el nombre del so
    String consulta;
          	  
    // tratar de procesar un voto y mostrar los resultados actuales
      
    try 
    {
	    // actualizar el total para la respuesta actual de la encuesta
	    consulta = "UPDATE encuesta SET valor = valor + 1 WHERE so = '"+so+"'";
	    //EJEMPLO= consulta = "UPDATE encuesta SET valor = valor + 1 WHERE so = 'Windows Vista'";
	    
	    statement.executeUpdate( consulta );
	
	    // obtener el total de todas las respuestas de la encuesta
	    consulta = "SELECT sum( valor ) FROM encuesta";
	    ResultSet totalRS = statement.executeQuery( consulta );
	    totalRS.next();
	    int total = totalRS.getInt( 1 );
	
	    // obtener resultados
	    consulta = "SELECT so, valor FROM encuesta ORDER BY so";
	    ResultSet resultadosRS = statement.executeQuery( consulta );
	         
	         
	         
	    salida.println( "<title>¡Gracias!</title>" );
	    salida.println( "</head>" );  
	    salida.println( "<body>" );  
	    salida.println( "<p>Gracias por su participación." );
	    salida.println( "<br />Resultados:</p><pre>" );
	         
	    // procesar los resultados
	    int votos;
	    while ( resultadosRS.next() ) 
	    	{
	            salida.print( resultadosRS.getString( 1 ) );
	            salida.print( ": " );
	            votos = resultadosRS.getInt( 2 );
	            salida.print( dosDigitos.format( 
	               ( double ) votos / total * 100 ) );
	            salida.print( "%  respuestas: " );
	            salida.println( votos );
	         }
	
	    resultadosRS.close();
	         
	    salida.print( "Total de respuestas: " );
	    salida.print( total );
	         
	    // fin del documento de XHTML
	    salida.println( "</pre></body></html>" );         
	    salida.close();

     

    // si ocurre una excepción en la base de datos, devolver página de error
    }catch ( SQLException excepcionSQL ) 
    	{
         excepcionSQL.printStackTrace();
         salida.println( "<title>Error</title>" );
         salida.println( "</head>" );  
         salida.println( "<body><p>Ocurrió un error en la base de datos. " );
         salida.println( "Intente de nuevo más tarde.</p></body></html>" );
         salida.close();
    	}

   }  // fin del método doPost

   // cerrar statementes de SQL y base de datos cuando termine el servlet
   
	public void destroy()
   {
      // tratar de cerrar statementes y conexion a la base de datos
      try 
      	{
         statement.close();
         conexion.close();
      	}

      // manejar excepciones de la base de datos devolviendo el error al cliente
      catch( SQLException excepcionSQL ) 
      	{
         excepcionSQL.printStackTrace();
      	}
   } 

}