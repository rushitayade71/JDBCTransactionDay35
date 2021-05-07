import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {


    public enum IOService { CONSOLE_IO, FILE_IO, DB_IO, REST_IO }
    private List<EmployeePayrollData> employeePayrollList;
    private  EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }


    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }


    public static void main(String[] args) {
        System.out.println("Welcome to Employee Payroll service program!");
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
}

    private void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Please enter employee name");
        String name = consoleInputReader.nextLine();
        System.out.println("Please enter employee ID");
        int id = consoleInputReader.nextInt();
        System.out.println("Please enter employee salary");
        double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        if(ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return  this.employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollForDataRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDataRange(startDate, endDate);
        return null;
    }


    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
    return null;
    }

    public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, gender, salary, startDate));

    }

    public void writeEmployeePayrollData(EmployeePayrollService.IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO)) {
            System.out.println("\n Writing Employee Payroll Roster to Console\n" + employeePayrollList);
        }
        else if(ioService.equals(IOService.FILE_IO)){
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
        }
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name, salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null) employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        EmployeePayrollData employeePayrollData;
        employeePayrollData = this.employeePayrollList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
        return employeePayrollData;
    }


    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public List<EmployeePayrollData> printEntries(IOService ioService){
        if(ioService.equals(IOService.FILE_IO));
        return new EmployeePayrollFileIOService().readData();
    }

    public long countEntries(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO));
        return new EmployeePayrollFileIOService().countEntries();
    }

    public void printData(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO));
        new EmployeePayrollFileIOService().printData();
    }
}