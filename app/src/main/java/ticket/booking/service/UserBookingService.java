package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserBookingService{
    
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;

    private static User user;

    private final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService(User loginAttemptUser) throws IOException {
        loadUserListFromFile();

        Optional<User> foundUser = userList.stream()
                .filter(user1 -> user1.getName().equals(loginAttemptUser.getName()) &&
                        UserServiceUtil.checkPassword(loginAttemptUser.getPassword(), user1.getHashedPassword()))
                .findFirst();

        if (foundUser.isPresent()) {
            this.user = foundUser.get();     // ✅ Assign real user object with saved tickets
        } else {
            throw new IOException("Login failed: User not found or invalid credentials.");
        }
    }


    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    public void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public String fetchBookings(){


        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
        else {
            System.out.println("User not present ::::");
        }
        return null;
    }

        // todo: Complete this function
         public Boolean cancelBooking(String ticketId){
    
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
        System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }
   private String Source ;
   private   String Dest ;

   public static String getUserName(){
       return user.getName();
   }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getDest() {
        return Dest;
    }

    public void setDest(String dest) {
        Dest = dest;
    }

    public List<Train> getTrains(String source, String destination){
          this.Source = source;
          this.Dest = destination;
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
            return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);

                    trainService.addTrain(train);

                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }




    public boolean releaseSeat(Train train, int row, int col) throws IOException {
        List<List<Integer>> seats = train.getSeats();
        if (row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size()) {
            if (seats.get(row).get(col) == 1) {
                seats.get(row).set(col, 0);  // mark as unbooked
                train.setSeats(seats);       // optional but safe

                TrainService trainService = new TrainService(); // ✅ Fix for null pointer
                trainService.updateTrain(train);                // ✅ Save changes

                return true;
            }
        }
        return false;
    }




    public User getUser() {
        return user;
    }


}
