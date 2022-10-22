package br.com.ifsp.jms.jms;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

public class TesteProdutorFila {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		
		Connection connection = factory.createConnection(); 
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Destination fila = (Destination) context.lookup("LOG");
		
		Destination topico = (Destination) context.lookup("pedidos");
		
		var listaErros = List.of("ERR", "WARN", "DEBUG", "");
		
		var lista = List.of("DiaDasCriancas", "DiaDasMaes", "DiaDosPais", "DiaNormal");
		
		Random random_method = new Random();
		
		for (int i = 0; i < 1000; i++) {
            // generating random index with the help of
            // nextInt() method
            int index = random_method.nextInt(lista.size());
            int indexErro = random_method.nextInt(listaErros.size());
  
            lista.get(index);
            
            if(!listaErros.get(indexErro).equals("") && i < 300) {
            	MessageProducer producer = session.createProducer(fila);
            	
            	Message error = session.createTextMessage(listaErros.get(indexErro) + "erro que deu");
        		
        		if(listaErros.get(indexErro).equals("ERR")) {
        			producer.send(error,DeliveryMode.NON_PERSISTENT, 9,300000);
        		}
        		
        		else if(listaErros.get(indexErro).equals("WARN")) {
        			producer.send(error,DeliveryMode.NON_PERSISTENT, 1,300000);
        		}
        		
        		else if(listaErros.get(indexErro).equals("DEBUG")) {
        			producer.send(error,DeliveryMode.NON_PERSISTENT,4,300000);
        		}
            } else {
            	
            	MessageProducer producer = session.createProducer(topico);
            	
				Message message = session.createTextMessage("<pedido><promocao>" + lista.get(index) + "<pedido><promocao><id>"  +  i + "</id></pedido>");
    			message.setBooleanProperty(lista.get(index), true);
				producer.send(message);			
            }
           
		}
				
		new Scanner(System.in).nextLine();
		
		session.close();
		connection.close();
		context.close();
	}
}