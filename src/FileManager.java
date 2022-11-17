import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    private final String charset;
    private final int writeBufferSize;
    private final int readBufferSize;
    private final boolean autoFlush;
    private final String tempDirectory;

    public FileManager( String charset, int writeBufferSize, int readBufferSize, boolean autoFlush, String tempDirectory) {
        this.charset = charset;
        this.writeBufferSize = writeBufferSize;
        this.readBufferSize = readBufferSize;
        this.autoFlush = autoFlush;
        this.tempDirectory = tempDirectory.isEmpty() ? "" : tempDirectory + File.separator;
    }

    public BufferedWriter createOutputStreamWriter(String fileName) throws Exception {
        return new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(tempDirectory + fileName)), charset), writeBufferSize);
    }

    public BufferedReader createBufferedReader(String fileName) throws Exception {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(tempDirectory + fileName)), charset), readBufferSize);
    }

    public void write(BufferedWriter writer, String item) throws IOException {
        writer.write(item);
        if (autoFlush)
            writer.flush();
    }

    public File getFile(String fileName) {
        return new File(tempDirectory + fileName);
    }

    public String getDirectory() {
        return tempDirectory;
    }
}
