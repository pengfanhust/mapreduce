package hw4.plain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class RecordKey implements WritableComparable<RecordKey> {

	private String airLineName;
	private int flightMonth;

	public void set(String airLineName, int flightMonth) {
		this.flightMonth = flightMonth;
		this.airLineName = airLineName;
	}

	public String getAirLineName() {
		return airLineName;
	}

	public int getFlightMonth() {
		return flightMonth;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(airLineName);
		out.writeInt(flightMonth);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		airLineName = in.readUTF();
		flightMonth = in.readInt();
	}

	@Override
	public int compareTo(RecordKey o) {
		if (!airLineName.equals(o.airLineName)) {
			return airLineName.compareTo(o.airLineName);
		} else if (flightMonth != o.flightMonth) {
			return flightMonth < o.flightMonth ? -1 : 1;
		} else {
			return 0;
		}
	}

	public String toString() {
		return airLineName.concat(" ").concat(String.valueOf(flightMonth));
	}

	public boolean equals(Object o) {
		if (o instanceof RecordKey) {
			return airLineName.equals(((RecordKey) o).airLineName)
					&& flightMonth == ((RecordKey) o).flightMonth;
		}
		return false;
	}

	public int hashCode() {
		return airLineName.hashCode();
	}
}
