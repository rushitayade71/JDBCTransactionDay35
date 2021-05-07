import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService() {

    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getconnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/emp_payroll";
        String username = "root";
        String password = "root";
        Connection connection = null;
        System.out.println("Connecting to database: " +url);
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Connection is successfull!!" +connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "SELECT * FROM employee_details;";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public List<EmployeePayrollData> getEmployeePayrollForDataRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM employee_details WHERE START BETWEEN '%s' AND '%s' ;",
                                    Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }



    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(salary) as avg_salary FROM employee_details GROUP BY gender; ";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getconnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try(Connection connection = this.getconnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try {
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name =resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getconnection();
            String sql = "SELECT * FROM employee_details WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingStatement(name,salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_details set salary = %.2f where name = '%s';",salary,name );
        try(Connection connection = this.getconnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate) {
        int employeeId = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_details ( name, gender, salary, start ) " +
                                   "VALUES ( '%s', '%s', '%s', '%s' )" , name, gender, salary, Date.valueOf(startDate));
        try(Connection connection = this.getconnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollData;
    }
}
