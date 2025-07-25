package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticket.booking.service.UserBookingService;

import java.sql.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Ticket{

    private String ticketId;

    private String userId;

    private String username;

    private String source;

    private String destination;

    private List<List<Integer>> seats;

    private String dateOfTravel;

    private Train train;

    private int col;

    private int row;









    public Ticket(){

  //      System.out.println("ticket dfault constructor");

    }

    public Ticket(String ticketId, String Username, String userId, String source, String destination, String dateOfTravel ){
        this.ticketId = ticketId;
        this.userId = userId;
        this.username = Username;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;


    }
    public Ticket(int row, int col){
        this.row = row;
        this.col = col;
    }


    public Ticket(int row, int col, String userName, String ticketId, String userId, String source, String destination, String dateOfTravel, Train train) {
        this.row = row;
        this.col = col;
        this.ticketId = ticketId;
        this.userId = userId;
        this.username = userName;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }

    public Ticket(String userName, String ticketId, String userId, String source, String destination, String dateOfTravel, Train trainSelectedForBooking) {

        this.ticketId = ticketId;
        this.userId = userId;
        this.username = userName;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = trainSelectedForBooking;
    }




    public String getTicketInfo(){

        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s row %s col %s", ticketId, userId, source, destination, dateOfTravel,row, col);
    }

    public String getTicketId(){
        return ticketId;
    }

    public void setTicketId(String ticketId){
        this.ticketId = ticketId;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }

    public String getDateOfTravel(){
        return dateOfTravel;
    }

    public void setDateOfTravel(String dateOfTravel){
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain(){
        return train;
    }

    public void setTrain(Train train){
        this.train = train;
    }




    public int getTrainNo() {
        try {
            return Integer.parseInt(train.getTrainNo());
        } catch (NumberFormatException | NullPointerException e) {
            return -1; // Return a default or invalid train number
        }
    }

    public int getCol() {
        return col; // 'col' is the seat number in your booking matrix
    }

    public int getRow() {
        return row; // 'row' is the row number in your booking matrix
    }


}

