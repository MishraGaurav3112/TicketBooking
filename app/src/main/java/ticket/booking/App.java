package ticket.booking;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.TrainService;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        try{
            userBookingService = new UserBookingService();
        }catch(IOException ex){
            System.out.println("There is something wrong");
            ex.printStackTrace();  // Add this line to see the actual error
            return;
        }

        Train trainSelectedForBooking = new Train();
        try{
            while(option!=7){
                System.out.println("Choose option");
                System.out.println("1. Sign up");
                System.out.println("2. Login");
                System.out.println("3. Fetch Bookings");
                System.out.println("4. Search Trains");
                System.out.println("5. Book a Seat");
                System.out.println("6. Cancel my Booking");
                System.out.println("7. Exit the App");
                System.out.println("8. delete all bookingg");
                option = scanner.nextInt();


                switch (option){
                    case 1:
                        System.out.println("Enter the username to signup");
                        String nameToSignUp = scanner.next();
                        System.out.println("Enter the password to signup");
                        String passwordToSignUp = scanner.next();
                        User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                        userBookingService.signUp(userToSignup);
                        break;
                    case 2:
                        System.out.println("Enter the username to Login");
                        String nameToLogin = scanner.next();
                        System.out.println("Enter the password to signup");
                        String passwordToLogin = scanner.next();
                        User userToLogin = new User(nameToLogin,
                                passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin),
                                new ArrayList<>(), UUID.randomUUID().toString());
                        try {
                            userBookingService = new UserBookingService(userToLogin);
                            System.out.println("Login successful.");
                        } catch (IOException ex) {
                            System.out.println("Login failed: " + ex.getMessage());
                            return;
                        }

                        break;
                    case 3:
                        System.out.println("Fetching your bookings");
                        userBookingService.fetchBookings();
                        break;
                    case 4:
                        System.out.println("Type your source station");
                        String source = scanner.next();
                        System.out.println("Type your destination station");
                        String dest = scanner.next();
                        List<Train> trains = userBookingService.getTrains(source, dest);
                        int index = 1;
                        for (Train t : trains) {
                            System.out.println(index + " Train id : " + t.getTrainId());
                            Map<String, String> stationTimes = t.getStationTimes();
                            if (stationTimes != null) {
                                for (Map.Entry<String, String> entry : stationTimes.entrySet()) {
                                    System.out.println("station " + entry.getKey() + " time: " + entry.getValue());
                                }
                            }
                            index++;
                        }

                        if (trains.isEmpty()) {
                            System.out.println("No trains found for the selected route.");
                            break;
                        }

                        System.out.println("Select a train by typing 1, 2, 3...");
                        int choice = scanner.nextInt();
                        if (choice >= 1 && choice <= trains.size()) {
                            trainSelectedForBooking = trains.get(choice - 1);
                            System.out.println("Train selected: " + trainSelectedForBooking.getTrainId());
                        } else {
                            System.out.println("Invalid train selection.");
                            break;
                        }

                        break;
                    case 5:
                        if (trainSelectedForBooking == null || trainSelectedForBooking.getTrainId() == null) {
                            System.out.println("⚠️ Please select a train first using option 4.");
                            break;
                        }

                        System.out.println("Select a seat out of these seats:");
                        List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);

                        if (seats == null || seats.isEmpty()) {
                            System.out.println("⚠️ No seat data available for this train.");
                            break;
                        }

                        for (List<Integer> rowList : seats) {
                            for (Integer val : rowList) {
                                System.out.print(val + " ");
                            }
                            System.out.println();
                        }

                        System.out.println("Select the seat by typing the row and column");
                        System.out.print("Enter the row: ");
                        int row = scanner.nextInt();
                        System.out.print("Enter the column: ");
                        int col = scanner.nextInt();
                        System.out.println("Booking your seat....");

                        Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                        if (Boolean.TRUE.equals(booked)) {
                            String ticketId = UUID.randomUUID().toString();
                            String dateOfTravel = java.time.LocalDate.now().toString();

                            Ticket ticket = new Ticket(
                                    row, col,
                                    UserBookingService.getUserName(),
                                    ticketId,
                                    userBookingService.getUser().getUserId(),
                                    userBookingService.getSource(),
                                    userBookingService.getDest(),
                                    dateOfTravel,
                                    trainSelectedForBooking
                            );

                            userBookingService.getUser().getTicketsBooked().add(ticket);
                            userBookingService.saveUserListToFile();

                            System.out.println("✅ Booking successful! Seat at row=" + row + ", col=" + col +
                                    " on train: " + trainSelectedForBooking.getTrainId());
                        } else {
                            System.out.println("❌ Cannot book this seat. It may already be taken.");
                        }
                        break;


                    case 6:
                        List<Ticket> bookedTickets = userBookingService.getUser().getTicketsBooked();
                        if (bookedTickets == null || bookedTickets.isEmpty()) {
                            System.out.println("You have no bookings to cancel.");
                            break;
                        }

                        System.out.println("Your current bookings:");
                        int ticketIndex = 1;
                        for (Ticket ticket : bookedTickets) {
                            String trainId = (ticket.getTrain() != null && ticket.getTrain().getTrainId() != null)
                                    ? ticket.getTrain().getTrainId()
                                    : "N/A";

                            System.out.println(ticketIndex + ". Ticket ID: " + ticket.getTicketId() +
                                    ", Train: " + trainId +
                                    ", Seat: Row " + ticket.getRow() + " Col " + ticket.getCol() +
                                    ", Source: " + ticket.getSource() +
                                    ", Destination: " + ticket.getDestination() +
                                    ", Date: " + ticket.getDateOfTravel());
                            ticketIndex++;
                        }

                        System.out.println("Enter the booking number to cancel (e.g., 1, 2, 3...):");
                        int cancelIndex = scanner.nextInt();
                        if (cancelIndex >= 1 && cancelIndex <= bookedTickets.size()) {
                            Ticket ticketToCancel = bookedTickets.remove(cancelIndex - 1);
                            boolean seatReleased = userBookingService.releaseSeat(ticketToCancel.getTrain(), ticketToCancel.getRow(), ticketToCancel.getCol());
                            userBookingService.saveUserListToFile();
                            if (seatReleased) {
                                System.out.println("Booking cancelled successfully for Ticket ID: " + ticketToCancel.getTicketId());
                            } else {
                                System.out.println("Seat release failed, but ticket removed.");
                            }
                        } else {
                            System.out.println("Invalid booking number.");
                        }
                        break;
                    case 8:
                        System.out.println("Fetching your bookings");
                        List<Ticket> bookedTickets2 = userBookingService.getUser().removeallTicketsBooked();

                        break;


                    default:
                        break;
                }
            }
        }catch (Exception e) {
            System.out.println("⚠️ Something went wrong during execution:");
            e.printStackTrace(); // ✅ Print full error to console
        }


    }
}
