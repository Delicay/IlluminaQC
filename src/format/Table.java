/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

package format;

import cern.colt.GenericSorting;
import cern.colt.Swapper;
import cern.colt.function.IntComparator;
import utils.FStringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Fei Lu
 */
public class Table {
    public String[] header;
    public String[][] content;
    private int sortColumnIndex;
    
    public Table (String infileS) {
        this.readTable(infileS);
    }
    
    public Table (String infileS, int rowNum) {
        this.readTable(infileS, rowNum);
    }
    
    public Table (String[] header, String[][] content) {
        this.header = header;
        this.content = content;
    }
    
    public Table (String infileS, String delimiter) {
        this.readTable(infileS, delimiter);
    }
    
    public Table getSubTableByColumn (Table at, int[] index) {
        String[] newHeader  = new String[index.length];
        String[][] newContent = new String[this.getRowNumber()][index.length];
        for (int i = 0; i <  newHeader.length; i++) {
            newHeader[i] = at.header[index[i]];
        }
        for (int i = 0; i < this.getRowNumber(); i++) {
            for (int j = 0; j < index.length; j++) {
                newContent[i][j] = this.content[i][index[j]];
            }
        }
        return new Table(newHeader,newContent);
    }
    
    public Table getMergeTableByColumn (Table at) {
        if (this.getRowNumber()!=at.getRowNumber()) {
            System.out.println("RowNumbers are not equal. Can't merge tables. Program quit.");
            System.exit(1);
        }
        String[] newHeader  = new String[this.getColumnNumber()+at.getColumnNumber()];
        String[][] newContent = new String[this.getRowNumber()][newHeader.length];
        System.arraycopy(this.header, 0, newHeader, 0, this.getColumnNumber());
        System.arraycopy(at.header, 0, newHeader, this.getColumnNumber(), at.getColumnNumber());
        for (int i = 0; i < this.getRowNumber(); i++) {
            System.arraycopy(this.content[i], 0, newContent[i], 0, this.getColumnNumber());
            System.arraycopy(at.content[i], 0, newContent[i], this.getColumnNumber(), at.getColumnNumber());
        }
        return new Table(newHeader,newContent);
    }
    
    public Table getMergeTableByRow (Table at) {
        if (this.getColumnNumber()!=at.getColumnNumber()) {
            System.out.println("Column numbers are not equal. Can't merge tables. Program quit.");
            System.exit(1);
        }
        String[][] newContent = new String[this.getRowNumber()+at.getRowNumber()][];
        for (int i = 0; i < this.getRowNumber(); i++) {
            newContent[i] = this.content[i];
        }
        for (int i = 0; i < at.getRowNumber(); i++) {
            newContent[i+this.getRowNumber()] = at.content[i];
        }
        return new Table(this.header,newContent);
    }
    
    public double getDoubleValue (int i, int j) {
        return Double.valueOf(this.content[i][j]);
    }
    
    public int getIntValue (int i, int j) {
        return Integer.valueOf(this.content[i][j]);
    }
    
    public int getRowNumber () {
        return content.length;
    }
    
    public int getColumnIndex (String name) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(name)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public int getColumnNumber() {
        return header.length;
    }
    
    public String[] getStringArrayByColumn (int columnIndex) {
        String[] temp = new String[this.getRowNumber()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = content[i][columnIndex];
        }
        return temp;
    }
    
    public int[] getIntArrayByColumn (int columnIndex) {
        int[] value = new int[this.getRowNumber()];
        for (int i = 0; i < value.length; i++) {
            value[i] = Integer.valueOf(content[i][columnIndex]);
        }
        return value;
    }
    
    public double[] getDoubleArrayByColumn (int columnIndex) {
        double[] value = new double[this.getRowNumber()];
        for (int i = 0; i < value.length; i++) {
            value[i] = Double.valueOf(content[i][columnIndex]);
        }
        return value;
    }
    
    private void readTable(String infileS) {
        int cnt = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(infileS),65536);
            header = br.readLine().split("\t",-1);
            String temp;
            while (((temp = br.readLine()) != null)) cnt++;
            content = new String[cnt][header.length];
            br.close();
            br = new BufferedReader(new FileReader(infileS),65536);
            br.readLine();
            for (int i = 0; i < content.length; i++) {
                content[i] = br.readLine().split("\t",-1);
                if ((i+1)%10000 == 0) System.out.println(String.valueOf(i+1) + " rows are read in Table");
            }
            System.out.println("Table " + infileS + " is load");
        }
        catch (Exception e) {
            System.out.println("Error occurred while reading Table file " + infileS);
        }
    }
    
    private void readTable (String infileS, int rowNum) {
        int cnt = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(infileS),65536);
            header = br.readLine().split("\t");
            String temp;
            while (((temp = br.readLine()) != null)) cnt++;
            if (cnt < rowNum) rowNum = cnt;
            content = new String[rowNum][header.length];
            br.close();
            br = new BufferedReader(new FileReader(infileS),65536);
            br.readLine();
            for (int i = 0; i < content.length; i++) {
                content[i] = br.readLine().split("\t");
                if (i%10000 == 0 && i != 0) System.out.println(i + " rows are read in Table");
            }
            System.out.println("Table " + infileS + " is load");
        }
        catch (Exception e) {
            System.out.println("Error occurred while reading Table file " + infileS);
        }
    }
    
    private void readTable(String infileS, String splitCha) {
        ArrayList<String> recordList = new ArrayList();
        String[] recordArray;
        try {
            BufferedReader br = new BufferedReader(new FileReader(infileS),65536);
            header = br.readLine().split(splitCha);
            String temp;
            while (((temp = br.readLine()) != null)) {
                recordList.add(temp);
            }
            recordArray = recordList.toArray(new String[recordList.size()]);
            content = new String[recordArray.length][header.length];
            for (int i = 0; i < content.length; i++) {
                content[i] = recordArray[i].split(splitCha);
            }
        }
        catch (Exception e) {
            System.out.println("Error occurred while reading Table file " + infileS);
        }
    }
    
    public void writeTable (String outfileS) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfileS), 65536);
            for (int i = 0; i < header.length; i++) {
                bw.write(header[i]+"\t");
            }
            bw.newLine();
            for (int i = 0; i < content.length; i++) {
                for (int j = 0; j < header.length; j++) {
                    bw.write(content[i][j]+"\t");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred while writing table file " + outfileS);
        }
        System.out.println("Table written in " + outfileS);
    }
    
    public void writeTable (String outfileS, String delimiter) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfileS), 65536);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < header.length; i++) {
                sb.append(header[i]).append(delimiter);
            }
            sb.delete(sb.length()-delimiter.length(), sb.length());
            bw.write(sb.toString());
            bw.newLine();
            for (int i = 0; i < content.length; i++) {
                sb = new StringBuilder();
                for (int j = 0; j < header.length; j++) {
                    sb.append(content[i][j]).append(delimiter);
                }
                sb.delete(sb.length()-delimiter.length(), sb.length());
                bw.write(sb.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred while writing table file " + outfileS);
        }
        System.out.println("Table written in " + outfileS);
    }
    
    public void writeTable (String outfileS, boolean[] ifout) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfileS), 65536);
            for (int i = 0; i < header.length; i++) {
                bw.write(header[i]+"\t");
            }
            bw.newLine();
            for (int i = 0; i < content.length; i++) {
                if (!ifout[i]) continue;
                for (int j = 0; j < header.length; j++) {
                    bw.write(content[i][j]+"\t");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred while writing table file " + outfileS);
        }
        System.out.println("Table written in " + outfileS);
    }
    
    /**
     * Output table from selected row or column
     * @param outfileS
     * @param indexList
     * @param ifRowIndex 
     */
    public void writeTable (String outfileS, int[] indexList, boolean ifRowIndex) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfileS), 65536);
            if (ifRowIndex) {
                for (int i = 0; i < header.length; i++) {
                    bw.write(header[i]+"\t");
                }
                bw.newLine();
                for (int i = 0; i < indexList.length; i++) {
                    for (int j = 0; j < header.length; j++) {
                        bw.write(content[indexList[i]][j]+"\t");
                    }
                    bw.newLine();
                    if (i%10000 == 0) System.out.println(i + " rows written in Table");
                }
            }
            else {
                bw.write(FStringUtils.join(header, indexList, "\t"));
                bw.newLine();
                for (int i = 0; i < this.getRowNumber(); i++) {
                    for (int j = 0; j < indexList.length-1; j++) {
                        bw.write(this.content[i][indexList[j]]+"\t");
                    }
                    bw.write(this.content[i][indexList[indexList.length-1]]+"\t"); 
                    bw.newLine();
                    if (i%10000 == 0) System.out.println(i + " rows written in Table");
                }
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred while writing table file " + outfileS);
        }
        System.out.println("Table written in " + outfileS);
    }
    
    public void sortByStringOnColumnName (String name) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(name)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            System.out.println("Cannot find column with name of " + name);
            System.exit(0);
        }
        this.sortByStringOnColumn(index);
    }
    
    public void sortByStringOnColumn (int columnIndex) {
        System.out.println("Start sorting table");
        sortColumnIndex = columnIndex;
        GenericSorting.quickSort(0, this.getRowNumber(), compStringByColumn, swapper);
        System.out.println("Finished sorting table");
    }
    
    public void sortByValueOnColumnName (String name) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(name)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            System.out.println("Cannot find column with name of " + name);
            System.exit(0);
        }
        this.sortByValueOnColumn(index);
    }
    
    public void sortByValueOnColumn (int columnIndex) {
        System.out.println("Start sorting table");
        sortColumnIndex = columnIndex;
        GenericSorting.quickSort(0, this.getRowNumber(), compValueByColumn, swapper);
        System.out.println("Finished sorting table");
    }
    
    Swapper swapper = new Swapper() {
        public void swap(int a, int b) {
        String[] temp = content[a];
        content[a] = content[b];
        content[b] = temp;
        }
    };
    
    IntComparator compStringByColumn = new IntComparator() {
        public int compare(int a, int b) {
            return content[a][sortColumnIndex].compareTo(content[b][sortColumnIndex]);
        }
    };
    
    IntComparator compValueByColumn = new IntComparator() {
        public int compare(int a, int b) {
            double va = Double.valueOf(content[a][sortColumnIndex]);
            double vb = Double.valueOf(content[b][sortColumnIndex]);
            if (va < vb) return -1;
            else if (va == vb) return 0;
            return 1;
        }
    };
    
}
