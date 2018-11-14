package Controller;

import Model.ProgramDeclaration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sukamto 23518017 Andreas Novian 23518002
 */
public class Controller {

    List<String> listReservedWord, listAlphanumeric, listCharacters;
    BufferedReader br;
    BufferedWriter bw;
    String isiFile = ""; //isi file murni, belum diapa-apain
    String[] listOfSymbols; //kumpulan simbol setelah dipisahin
    String symbol; //simbol yang sedang diproses
    int cursor; //penunjuk simbol yang sedang dicek
    boolean isError = false;

    public Controller() throws FileNotFoundException, IOException {
        initList();
        br = new BufferedReader(new FileReader("input.txt"));
    }

    private void initList() throws FileNotFoundException, IOException {
        //simpan isi file reservedWord ke list
        br = new BufferedReader(new FileReader("reservedWord.txt"));
        listReservedWord = new ArrayList<>();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            listReservedWord.add(currentLine);
        }

        //simpan isi file alphanumeric ke list
        br = new BufferedReader(new FileReader("alphanumeric.txt"));
        listAlphanumeric = new ArrayList<>();
        while ((currentLine = br.readLine()) != null) {
            listAlphanumeric.add(currentLine);
        }

        //simpan isi file characters ke list
        br = new BufferedReader(new FileReader("characters.txt"));
        listCharacters = new ArrayList<>();
        while ((currentLine = br.readLine()) != null) {
            listCharacters.add(currentLine);
        }
    }

    public void start() throws IOException {
        bw = new BufferedWriter(new FileWriter(new File("output.txt")));
        cursor = 0;
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            isiFile += currentLine + "\n";
        }
        br.close();

        listOfSymbols = preprocess(isiFile);
        symbol = listOfSymbols[0];

        new ProgramDeclaration(this).procedureA();
        if (cursor < listOfSymbols.length - 2) {
            if (!isError) {
                bw.write("(Error)");
            }
            for (int i = cursor; i < listOfSymbols.length; i++) {
                bw.write(listOfSymbols[i]);
            }
            isError = true;
        }
        if (!isError) {
            bw.write("\nTidak ada error\n");
        }
        bw.close();
        br.close();
    }

    public void accept(String terminal) throws IOException {
        System.out.println("terminal - symbol = " + terminal + " - " + symbol);
        if (terminal.equalsIgnoreCase(symbol)) {
            bw.write(symbol);
            readNextSymbol();
        } else {
            isError = true;
            bw.write("(Error)" + symbol);
            readNextSymbol();
        }
    }

    private void readNextSymbol() {
        cursor++;
        if (cursor < listOfSymbols.length) {
            this.symbol = listOfSymbols[cursor];
        }
    }

    public String getSymbol() {
        return this.symbol;
    }

    private String[] preprocess(String isiFile) throws IOException {
        String temp = "";
        String lastKnown = "";
//        boolean inserted = false; //untuk tahu pernah dimasukkin ke simbol atau belum
        char c;

        isiFile = isiFile.replaceAll("\\s+", "");
        List<String> result = new ArrayList<>();

        for (int i = 0; i < isiFile.length(); i++) {
            c = isiFile.charAt(i);
            temp += c;

            if (isReservedWord(lastKnown + temp)) {
                for (int j=0;j<lastKnown.length();j++){
                    result.remove(""+lastKnown.charAt(j));
                }
                result.add(lastKnown + temp);
                lastKnown = "";
                temp = "";
            } else if (isCharacters(lastKnown + temp)) {
                for (int j=0;j<lastKnown.length();j++){
                    result.remove(""+lastKnown.charAt(j));
                }
                result.add(lastKnown + temp);
                lastKnown = "";
                temp = "";
            } else {
                result.add(temp);
                lastKnown += temp;
                temp = "";
            }
        }

        String[] arrResult = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            arrResult[i] = result.get(i);
        }
        return arrResult;
    }

    private boolean isReservedWord(String in) {
        for (String currentLine : listReservedWord) {
            if (currentLine.equalsIgnoreCase(in)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlphanumeric(String in) {
        for (String currentLine : listAlphanumeric) {
            if (currentLine.equalsIgnoreCase(in)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCharacters(String in) {
        for (String currentLine : listCharacters) {
            if (currentLine.equalsIgnoreCase(in)) {
                return true;
            }
        }
        return false;
    }
}