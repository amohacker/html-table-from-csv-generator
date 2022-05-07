import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main {
    private static File inputFile;
    private static File outputFile;
    private static Boolean header;
    private static boolean scripts;
    private static ArrayList<String> colnames = new ArrayList<>();
    private static char seperator;

    public static void main(String[] args) throws IOException {
        System.out.println("CSV to html table converter by AMOnDuck.");
        getUserinfo();
        List list = csvImport(inputFile, colnames);
        generateHTML(list, outputFile);

    }

    public static void getUserinfo() {
        int colnum;

        //get input file
        while (true) {
            try {
                System.out.print("Enter input file: ");
                String fileinput = getInput();
                inputFile = testfile(fileinput);
                break;
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("Please try again.");
            }
            System.out.println();
        }
        while (true)
            try {
                System.out.print("Enter output file: ");
                outputFile = new File(getInput());
                break;
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("An error has occured, please try again.");
            }

        System.out.print("Separator (e.g. ','): ");
        seperator = getInput().charAt(0);
        while (true) {
            System.out.print("Does the file have a header?(y/n) ");
            String input = getInput();
            if (input.equals("y")) {
                header = true;
                break;
            } else if (input.equals("n")) {
                header = false;
                break;
            } else {
                System.out.println("Please enter y or n.");
            }
        }
        while (true) {
            System.out.print("Do you wish to include interactivity scripts?(y/n) ");
            String input = getInput();
            if (input.equals("y")) {
                scripts = true;
                break;
            } else if (input.equals("n")) {
                scripts = false;
                break;
            } else {
                System.out.println("Please enter y or n.");
            }
        }
        if (header == false) {
            //get number of columns
            while (true) {
                try {
                    System.out.print("Number of columns: ");
                    colnum = Integer.valueOf(getInput());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number.");
                }
            }
            //get column names
            for (int i = 0; i < colnum; i++) {
                System.out.print(new StringBuilder().append("Enter column name ").append(i + 1).append(": ").toString());
                colnames.add(getInput());
            }
        }
    }

    //gets user input
    public static String getInput() {
        Scanner sc = new Scanner(System.in);
        return sc.next();
    }
    //validates inputted path and returns file object
    public static File testfile(String fileinput) throws FileNotFoundException {
         File file = new File(fileinput);
         if (file.isFile()) {
             return file;
         } else {
             throw new FileNotFoundException(file.getAbsolutePath() + " is invalid.");
         }
    }

    public static List csvImport(File file, ArrayList<String> colnames) throws IOException {
        Reader reader = Files.newBufferedReader(file.toPath());
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(seperator)
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();
        List<String[]> list = csvReader.readAll();
        reader.close();
        csvReader.close();
        if (header == true) {
            for (String str: list.get(0)) {
                colnames.add(str);
            }
            list.remove(0);

        }
        return list;
    }

    private static void generateHTML(List list, File oFile) throws IOException {
        int randomid = new Random().ints(1).findAny().getAsInt();
        if (randomid < 0) {
            randomid = randomid * -1;
        }
        try {
            oFile.createNewFile();
        } catch (IOException e) {
            while (true) {
                System.out.print("Output file already exists, continue? (y/n)");
                String input = getInput();
                if (input.equals("y")) {
                    break;
                } else if (input.equals("n")) {
                    while (true)
                        try {
                            System.out.print("Enter new output file: ");
                            outputFile = new File(getInput());
                            break;
                        } catch (Exception exeption) {
                            System.out.println(exeption);
                            System.out.println("An error has occured, please try again.");
                        }
                    break;
                } else {
                    System.out.println("Please enter y or n.");
                }
            }
        }

        FileWriter writer = new FileWriter(oFile.getAbsolutePath());
        if (scripts==true){
            writer.write("<script src=\"https://code.jquery.com/jquery-3.5.1.js\"></script>\n" +
                    "<script src=\"https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js\"></script>\n" +
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css\">\n");
        }
        writer.write("<script>\n    $(document).ready(function() {\n    $('#table"+randomid+"').DataTable();\n} );\n</script>\n");
        writer.write("<table id=\"table"+randomid+"\" class=\"display\" style=\"width:100%\">\n");
        writer.write("        <thead>\n            <tr>\n");
        for (String str: colnames) {
            writer.write(new StringBuilder().append("                <th>").append(str).append("</th>\n").toString());
        }
        writer.write("            </tr>\n" +
                "        </thead>\n");
        writer.write("        <tbody>\n");
        for (Object strings: list) {
            writer.write("            <tr>\n");
            for (String string : (String[]) strings) {
                writer.write(new StringBuilder().append("                <td>").append(string).append("</td>\n").toString());
            }
            writer.write("            </tr>\n");
        }
        writer.write("        </tbody>\n" +
                "        <tfoot>\n" +
                "            <tr>\n");
        for (String str: colnames) {
            writer.write(new StringBuilder().append("                <th>").append(str).append("</th>\n").toString());
        }
        writer.write("            </tr>\n" +
                "        </tfoot>\n" +
                "    </table>\n");
        writer.close();
    }
}
