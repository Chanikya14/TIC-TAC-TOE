package Q2 ;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {  

    private ServerSocket server = null; // done
    private Socket socket1 = null; // done
    private Socket socket2 = null; // done
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Integer ID = null; // done
    private Board board = null ;
    private Scanner sc = new Scanner(System.in);

    /*
     * server socket allows this player to accept connection from other player
     * socket1 is the accepted connection from server socket
     * socket2 is the socket that connects to the server of the other player
     * NOTE: Socket(HOST, port) throws ConnectException if no server is listening on port
     *       handle this while creating socket2
     *       Since server.accept is blocking, so order in which sockets are initialised must be considered appropriately.
     */


    /*
     * TODO: Add any variables you need
     */

    private void printResult(int winner, int ID) {
        /*
         * Don't change this function
         */
        if(winner == 0) {
            System.out.println("DRAW") ;
        } 
        else if(winner == ID) {
            System.out.println("YOU WON") ;
        }
        else System.out.println("YOU LOSE") ;
    }

    private Integer[] takeInput(){
        /*
         * TODO: Take input from the user from STDIN and return the position as an array
         * 
         */
         int c=0,d=0;
        while(true){
             System.out.print("Enter the position: ");

        /* take x and y as space separated integer*/
        // String[] pos = sc.nextLine();   
        c = sc.nextInt();
        d = sc.nextInt();
        if(board.available(c,d)) {
            break;
        }
        else {
            System.out.println("INVALID INPUT! Enter again");
        }
        }
    
        Integer[] arr = new Integer[2];
        arr[0] = c;
        arr[1] = d;
        return arr;
        /* check if the x and y position is valid and available on the board [x,y in {0,1,2}X{0,1,2}]
         * if not valid or not available, print "INVALID INPUT! Enter again" (without quotes) and ask for input again
        */

        
    }
    private void sendMove(Integer[] pos){
        /*
         * TODO: Send the move to other player by writing to the appropriate socket
         * sent string format: "x y" (space separated)
         */
         String s = Integer.toString(pos[0])+" "+Integer.toString(pos[1]);
         out.println(s);
    }

    private Integer[] recieveMove()
    throws IOException{
        /*
         * TODO: Recieve the move from the server by reading from the appropriate socket
         * return the move as an array of two integers
         * recieve format: "x y" (space separated)
         */
        String s = in.readLine();
        String[] sp = s.split(" ");
        Integer[] arr = new Integer[2];
        arr[0] = Integer.parseInt(String.valueOf(sp[0]));
        arr[1] = Integer.parseInt(String.valueOf(sp[1]));
        return arr;
    }
    public Client(Integer ID, Integer port1, Integer port2){
        /*
        * port1 is the port on which server is running and 
        * this player will accept connection from other player
        * 
        * port2 is the port on which the server of the other player is running
        */     

        /*
        * TODO: Initialize ID
        * Also initialize the board
        */
        this.ID = ID;
        board = new Board();
        try{
            /*
            * INITIALIZE THE ServerSocket 
            */
            server = new ServerSocket(port1);

            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            
            /* Don't change above print statements */
            Boolean iscon = false;
            while(!iscon) {
                try {
                    socket2 = new Socket("localhost",port2);
                    iscon = true;
                    socket1 = server.accept();                   
                }
                catch(ConnectException e) {
                    continue;
                }
            }
            

            /*
            * INITIALIZE both socket variables, one that connects to other player and other accepting connection on server socket
            * NOTE: Socket(HOST, port) throws java.net.ConnectException if no server is listening on port
            * this happens when the other player is not yet ready to play i.e. has not started the server

            * handle this by trying to create the socket conneting to other player again and again in a loop until it is created
            * incase of exception just continue the loop
            */

            in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            out = new PrintWriter(socket1.getOutputStream() ,true);

            /*
             * initialize the input and output streams appropriately
             */

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void runClient () 
    throws IOException{
        Boolean myturn = ID==1;
        board.printBoard();
        while(true) {
                /*
                * Here we would have the game logic
                * You can use the functions defined above
                * NOTE: for player with ID 1, it is your turn initially
                *       for player with ID 2, it is the other player's turn initially
                * We have implemented the game using a logic which utilizes myturn bool. You are welcome to try any other logic.
                */



            if(myturn) {
                /* your turn */
                Integer[] arr = takeInput();
                board.updateBoard(arr,ID);
                sendMove(arr);
                int c = board.checkBoard();
                if(c>=0) {
                    printResult(c,ID);
                }
                if(c==1 || c==2) break;
                if(c==0) break;
            }
            else {
                /* other player's turn */
                Integer[] arr = recieveMove();
                Integer tmp=0;
                if(ID==1) tmp = 2;
                if(ID == 2) tmp =1;
                board.updateBoard(arr,tmp);
                int c = board.checkBoard();
                if(c>=0) {
                    printResult(c,ID);
                }
                if(c==1 || c==2) break;
                if(c==0) break;
            }
            myturn = !myturn;
        }
    }

    public static void main(String args[]) {
        /*
         * Don't change this function
         */
        Integer ID = Integer.parseInt(args[0]) ; // from the terminal
        System.out.println(ID);
        if(ID != 1 && ID != 2) {
            System.out.println("Incorrect Player ID\n");
            return ;
        }
        Integer port1 = Integer.parseInt(args[1]) ;
        Integer port2 = Integer.parseInt(args[2]) ;
        try {
            Client client = new Client(ID, port1, port2) ;
            client.runClient() ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
