/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;


	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try 
		{
			file = new RandomAccessFile(fileName, "rw");

		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		} 

		finally {
			closeFile();
		}
	}
	
	public void closeFile() {
		try
		{
			if (output != null)
				output.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}


	public void openWriteFile(String fileName) {
		try
		{
			output = new RandomAccessFile(fileName, "rw");
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		} 
	}




	public long addRecords(Employee employeeToAdd) {
		Employee newEmployee = employeeToAdd;
		long currentRecordStart = 0;

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try 
		{
			record = new RandomAccessEmployeeRecord(newEmployee.getEmployeeId(), newEmployee.getPps(),
					newEmployee.getSurname(), newEmployee.getFirstName(), newEmployee.getGender(),
					newEmployee.getDepartment(), newEmployee.getSalary(), newEmployee.getFullTime());

			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length();
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;


	}
	
	
	public void changeRecords(Employee newDetails, long byteToStart) {
		long currentRecordStart = byteToStart;

		RandomAccessEmployeeRecord record;
		Employee oldDetails = newDetails;
		try 
		{
			record = new RandomAccessEmployeeRecord(oldDetails.getEmployeeId(), oldDetails.getPps(),
					oldDetails.getSurname(), oldDetails.getFirstName(), oldDetails.getGender(),
					oldDetails.getDepartment(), oldDetails.getSalary(), oldDetails.getFullTime());

			output.seek(currentRecordStart);
			
			record.write(output);
		} 

		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 
	}


	public void deleteRecords(long byteToStart) {
		long currentRecordStart = byteToStart;


		RandomAccessEmployeeRecord record;
		;

		try 
		{
			record = new RandomAccessEmployeeRecord();
			output.seek(currentRecordStart);
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}


	public void openReadFile(String fileName) {
		try 
		{
			input = new RandomAccessFile(fileName, "r");
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		} 
	}





	public long getFirst() {
		long byteToStart = 0;

		try {
			input.length();
		} 
		catch (IOException e) {
		}
		
		return byteToStart;
	}


	public long getLast() {
		long byteToStart = 0;

		try {
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		} 
		catch (IOException e) {
		}

		return byteToStart;
	}


	public long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);
			// if next position is end of file go to start of file, else get next position
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		} 
		catch (NumberFormatException e) {
		} 
		catch (IOException e) {
		}
		return byteToStart;
	}


	
	public long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException e) {
		} // end catch
		catch (IOException e) {
		}// end catch
		return byteToStart;
	}// end getPrevious

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		Employee thisEmp = null;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read file and get record
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		} // end try
		catch (IOException e) {
		}// end catch
		
		thisEmp = record;

		return thisEmp;
	}// end readRecords

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long oldByteStart = currentByteStart;
		long currentByte = 0;

		try {// try to read from file and look for PPS Number
			// Start from start of file and loop until PPS Number is found or search returned to start position
			while (currentByte != input.length() && !ppsExist) {
				//if PPS Number is in position of current object - skip comparison
				if (currentByte != oldByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);// Get record from file
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}// end if
				}// end if
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		} // end try
		catch (IOException e) {
		}// end catch

		return ppsExist;
	}// end isPpsExist

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read from file and look for ID
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);// Get record from file
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		}// end try
		catch (IOException e) {
		}// end catch

		return someoneToDisplay;
	}// end isSomeoneToDisplay
}// end class RandomFile
