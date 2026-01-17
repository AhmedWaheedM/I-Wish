package clientSide;

public class ResponseHandler {
    
    public static  void handleResponse(Object response) {

        if (response == null) {
            System.out.println("Received null response");
            return;
        }

        // Example:

        System.out.println("Received response: " + response);
    }
}
