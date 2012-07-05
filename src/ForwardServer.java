import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ForwardServer implements Runnable {

	public static final int PORT1 = 12333;
	public static final int PORT2 = 12335;
	
	public static Socket socket1;
	public static Socket socket2;

	public static Output output1;
	public static InputThread inputThread1;

	public static Output output2;
	public static InputThread inputThread2;

	public ForwardServer(Socket s1, Socket s2) {
		socket1 = s1;
		socket2 = s2;

		output1 = new Output(socket1);
		output2 = new Output(socket2);

		inputThread1 = new InputThread(socket1, output2);
		inputThread2 = new InputThread(socket2, output1);
	}

	public void run() {
		new Thread(inputThread1).start();
		new Thread(inputThread2).start();
	}

	public class Output {
		PrintWriter printWriter;
		public Output(Socket socket) {
			try {
				printWriter = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void send(String msg) {
			printWriter.println(msg);
		}
	}
	
	public class InputThread implements Runnable {
		BufferedReader in;
		Output out;
		String userInput;

		public InputThread(Socket socket, Output out) {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = out;
			} catch (Exception e) {
				System.err.println("Don't know about host: taranis.");
				System.exit(1);
			}

		}
		public void run() {
			try {
				userInput = in.readLine();
			} catch (Exception e) {}
			while ( userInput != null) {
				System.out.println("Listening on port ");
				out.send(userInput);
				try {
					userInput = in.readLine();
				} catch (Exception e) {}
			}
		}
	}

	public static void main(String[] args) {
		ServerSocket serverSocket1;
		ServerSocket serverSocket2;

		// build serversocket 1
		try {
			System.out.println("Creating ServerSocket(PORT1)");
			serverSocket1 = new ServerSocket(PORT1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// build serversocket 2
		try {
			System.out.println("Creating ServerSocket(PORT2)");
			serverSocket2 = new ServerSocket(PORT2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// accept socket1 (blocking)
		try {
			System.out.println("Wating on serverSocket1.accept()");
			socket1 = serverSocket1.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// accept socket2 (blocking)
		try {
			System.out.println("Wating on serverSocket2.accept()");
			socket2 = serverSocket2.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Running the ForwardServer");
		ForwardServer fs = new ForwardServer(socket1, socket2);

		fs.run();
	}

}
