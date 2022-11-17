import java.io.*;
import java.util.Arrays;

public class Sorter {
    private final StringHeap heap;
    private int runs;
    private final int maxFiles;
    private final FileManager fileManage;
    private BufferedWriter currentStream;
    private BufferedWriter[] writers;
    private boolean[] writeIsClosed;
    private int[] fibSequence;
    private int[] currentRuns;
    private int totalRuns = 1;
    private int totalPasses = 0;
    private String outputFile;

    public Sorter(int bufferSize, int maxFiles, String tempDirectory) {
        heap = new StringHeap(bufferSize);
        this.maxFiles = maxFiles;

        fileManage = new FileManager("UTF-8", 8192, 8192, false, tempDirectory);
    }

    public void sort(String[] data, String outputFile) {

        this.outputFile = outputFile;
        fibSequence = fibSequence(data.length / (heap.capacity() * 2) + 1, maxFiles);

        currentRuns = new int[maxFiles - 1];
        currentRuns[0] = 1;

        writers = new BufferedWriter[maxFiles];
        writeIsClosed = new boolean[maxFiles];

        int capacity = heap.capacity();

        int[] numArr = Arrays.stream(data).mapToInt(Integer::parseInt).toArray();

        Arrays.sort(numArr);

        for (int i = 0; i < capacity && i < data.length; i++) {
            heap.insert(String.valueOf(numArr[i]));
        }

        int j=0;
        if (heap.size() > 0) {
            runs = runFunction();

            if (writers[runs] != null) putStreamRuns("");

            while (j != numArr.length) {
               putStreamRuns(String.valueOf(numArr[j]));
                j++;
            }
        }

        int backup = runs;

        while ((runs = runFunctionDummy()) != -1) {
            putStreamRuns("");
        }
        runs = backup;

        System.out.println("\nTotal runs: " + totalRuns);

        for (int k = 0; k < maxFiles - 1; k++) {
            if (writers[k] != null) {
                try {
                    writers[k].close();
                    writeIsClosed[k] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        merge();
    }

    private void deleteFile(String fileName) {
        try {
            File file = fileManage.getFile(fileName);
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int numReaders(BufferedReader[] fr) {
        int out = 0;
        for (BufferedReader bufferedReader : fr) {
            if (bufferedReader != null)
                out++;
        }
        return out;
    }

    private void merge() {
        Heap mergeHeap = new Heap(maxFiles - 1);

        BufferedReader[] fileReaders = new BufferedReader[maxFiles];

        int finalRun = -1;

        boolean first = true;

        runs = maxFiles - 1;

        while (numReaders(fileReaders) > 1 || first) {
            first = false;

            int tempruns = -1;

            for (int i = 0; i < maxFiles; i++) {
                String inLine = null;
                try {
                    if (i == runs || !fileManage.getFile("file" + i).exists())
                        continue;

                    if (fileReaders[i] == null)
                        fileReaders[i] = fileManage.createBufferedReader("file" + i);

                    inLine = fileReaders[i].readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (inLine == null) {
                    tempruns = i;
                    try {
                        assert fileReaders[i] != null;
                        fileReaders[i].close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    deleteFile("file" + i);
                    fileReaders[i] = null;

                    totalPasses++;
                } else if (!inLine.isEmpty()) mergeHeap.insert(new Pair<>(i, inLine));
            }

            if (currentStream != null)
                putStreamMerge("");

            while (mergeHeap.size() > 0) {
                Pair<Integer, String> out = mergeHeap.peek();
                int key = out.key;

                String inLine = null;
                try {
                    inLine = fileReaders[key].readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (inLine == null) {
                    mergeHeap.get();
                    tempruns = key;
                    try {
                        fileReaders[key].close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    deleteFile("file" + key);
                    fileReaders[key] = null;

                    totalPasses++;
                } else {
                    if (inLine.isEmpty())
                        mergeHeap.get();
                    else
                        mergeHeap.replace(new Pair<>(key, inLine));
                }

                putStreamMerge(out.value);
            }

            if (tempruns != -1) {
                finalRun = runs;
                runs = tempruns;
                try {
                    currentStream.close();
                    currentStream = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\nTotal Passes: " + (totalPasses - (maxFiles - 2)));

        if (outputFile != null && new File(outputFile).exists())
            deleteFile(outputFile);

        if (outputFile == null) {
            try {
                FileInputStream br = new FileInputStream(fileManage.getDirectory() + "file" + (finalRun));

                byte[] buffer = new byte[1024];
                int length;
                while ((length = br.read(buffer)) > 0) {
                    System.out.write(buffer, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.flush();
        } else {
            File old = fileManage.getFile("file" + finalRun);
            File output = fileManage.getFile(outputFile);

            if (old.renameTo(output))
                System.out.println("FINISHED\n");
            else
                System.out.println("Error in renaming final output\n");
        }
    }

    private int runFunction() {
        totalRuns++;
        int puttable = (runs + 1) % (maxFiles - 1);
        int firstOneChecked = puttable;

        while (currentRuns[puttable] >= fibSequence[puttable + 1]) {
            puttable = (puttable + 1) % (maxFiles - 1);
            if (puttable == firstOneChecked) {
                fibNext(fibSequence);
            }
        }
        currentRuns[puttable] += 1;
        return puttable;
    }

    private int runFunctionDummy() {
        totalRuns++;
        int puttable = (runs + 1) % (maxFiles - 1);
        int firstOneChecked = puttable;

        while (currentRuns[puttable] >= fibSequence[puttable + 1]) {
            puttable = (puttable + 1) % (maxFiles - 1);
            if (puttable == firstOneChecked)
                return -1;
        }

        currentRuns[puttable] += 1;
        return puttable;
    }

    private void putStreamRuns(String item) {
        if (writers[runs] == null || writeIsClosed[runs]) {
            writeIsClosed[runs] = false;
            try {
                writers[runs] = fileManage.createOutputStreamWriter("file" + runs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            fileManage.write(writers[runs], item + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putStreamMerge(String item) {
        if (currentStream == null) {
            try {
                currentStream = fileManage.createOutputStreamWriter("file" + runs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            fileManage.write(currentStream, item + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fibNext(int[] previousSequence) {
        int output = -1;
        for (int i = 0; i < previousSequence.length; i++) {
            if (previousSequence[i] == 0)
                output = i;
        }
        if (output == -1)
            throw new Error("No zeros");

        for (int i = 0; i < maxFiles; i++) {
            previousSequence[i] += previousSequence[previousSequence.length - 1];
        }

        previousSequence[previousSequence.length - 1] = 0;
        {
            Arrays.sort(previousSequence);
        }
    }

    private int[] fibSequence(int dataLength, int maxFiles) {
        int[] returnArray = new int[maxFiles];
        returnArray[maxFiles - 1] = 1;
        int output = maxFiles - 1;
        int total = 1;

        while (true) {
            for (int i = 0; i < maxFiles; i++) {
                if (i != output) {
                    returnArray[i] += returnArray[output];
                    total += returnArray[i];
                }
            }
            returnArray[output] = 0;
            if (total >= dataLength) {
                Arrays.sort(returnArray);
                return (returnArray);
            }
            total = 0;
            output--;

            if (output < 0) {
                output += (maxFiles);
            }
        }
    }
}
