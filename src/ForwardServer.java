import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ForwardServer implements Runnable {

	public static final int PORT1 = 12333;
	public static final int PORT2 = 12335;
	/**
	 * @param args
	 */

	public static Socket socket1;
	public static Socket socket2;

	public static OutThread outThread1;
	public static InputThread inputThread1;

	public static OutThread outThread2;
	public static InputThread inputThread2;

	public ForwardServer(Socket s1, Socket s2) {
		socket1 = s1;
		socket2 = s2;

		outThread1 = new OutThread(socket1);
		outThread2 = new OutThread(socket2);

		inputThread1 = new InputThread(socket1, outThread2);
		inputThread2 = new InputThread(socket2, outThread1);
	}

	public void run() {
		new Thread(inputThread1).start();
		new Thread(inputThread2).start();
	}

	public class InputThread implements Runnable {
		BufferedReader in;
		OutThread out;
		String userInput;

		public InputThread(Socket socket, OutThread out) {
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
	public class OutThread {
		PrintWriter printWriter;
		public OutThread(Socket socket) {
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
	// prepare the output thread
	// prepare the input thread

	public static void main(String[] args) {
		ServerSocket serverSocket1;
		ServerSocket serverSocket2;

		// TODO Auto-generated method stub
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
		// build new thread - thread1 - with socket1 in and socket 2 out
		// run thread1
		System.out.println("Running the ForwardServer");
		ForwardServer fs = new ForwardServer(socket1, socket2);
//		public static OutThread outThread1;
//		public static InputThread inputThread1;


		// build new thread - thread2 - with socket2 in and socket 1 out
		// run thread2
		fs.run();



	}

}
