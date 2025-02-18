package app.labs.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pallet {
	int palletId;
	float load;
	float width;
	float depth;
	float height;
	String destination;
	String vehicleNumber;
}