import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

class Session implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private static final long timeout = 3000;
    private File file;

    Session(Socket _socket) throws IOException {
        socket = _socket;
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            //Todo: client's uuid
            file = createNotExistedFile(readFileName());

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                long fileLength = input.readLong();
                recvFile(fileLength, fileOut);
            } catch (IOException e) {
                System.out.println("Error while receiving file:" + e.getMessage());

                sendFinalMessage("Failed!");
                return;
            }

            sendFinalMessage("Success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void recvFile(long length, FileOutputStream fileOut) throws IOException {
        int buffSize = 1024;
        byte[] buffer = new byte[buffSize];

        long readLength = 0;
        long startTime = System.currentTimeMillis();
        long prevShowTime = startTime - 1000;

        long readWithOneIter = 0;
        while (readLength < length) {
            proceedSpeed(length, readLength, readWithOneIter, startTime, prevShowTime);

            readWithOneIter = 0;
            prevShowTime = System.currentTimeMillis();

            try {
                long currStartTime = System.currentTimeMillis(), currEndTime = startTime;

                do {
                    socket.setSoTimeout((int)timeout - (int)(currEndTime - currStartTime));

                    long readNow;
                    if (length - readLength >= buffSize) {
                        readNow = input.read(buffer);
                    } else {
                        readNow = input.read(buffer, 0, (int) (length - readLength));
                    }

                    fileOut.write(buffer, 0, (int) readNow);
                    readWithOneIter += readNow;
                    readLength += readNow;
                    currEndTime = System.currentTimeMillis();
                } while (currEndTime - currStartTime < timeout && readLength < length);

            } catch (SocketTimeoutException ignored) {
                continue;
            }
        }
        proceedSpeed(length, length, readWithOneIter, startTime, prevShowTime);
    }


    private String getSpeedMess(double speed) {
        if (speed <= 1024) {
            return String.format("%8.2f  B/s|", speed);
        } else if (speed <= 1024 * 1024) {
            return String.format("%8.2f KB/s|", speed / 1024);
        } else if (speed <= 1024 * 1024 * 1024) {
            return String.format("%8.2f MB/s|", speed / 1024 / 1024);
        } else {
            return String.format("%8.2f GB/s|", speed / 1024 / 1024 / 1024);
        }
    }

    private void proceedSpeed(long fileLength, long readAll, long readNow, long startTime, long prevShowTime) {
        long timeNow = System.currentTimeMillis();

        double speed = (double) (readAll) / (timeNow - startTime) * 1000;
        double instantSpeed = (double) (readNow) / (timeNow - prevShowTime) * 1000;
        //Todo: client's uuid
        String mess = String.format("|%-15s|", socket.getInetAddress());
        mess += getSpeedMess(speed);
        mess += getSpeedMess(instantSpeed);
        mess += String.format("%6.2f %%|", (double) readAll / fileLength * 100);
        mess += String.format("  %s|", file.getName());

        System.out.println(mess);
    }

    private String readFileName() throws IOException {
        int nameLength = input.readInt();
        byte[] nameBytes = new byte[nameLength];

        input.readFully(nameBytes, 0, nameLength);
        return new String(nameBytes, StandardCharsets.UTF_8);
    }

    private File createNotExistedFile(String fileName) {
        int slashPos = fileName.lastIndexOf("/");
        if (slashPos != -1) {
            fileName = fileName.substring(slashPos);
        }


        String prefix = fileName;
        String postfix = "";
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos != -1) {
            prefix = fileName.substring(0, dotPos);
            postfix = fileName.substring(dotPos);
        }

        String newFilename = prefix + postfix;
        File file;
        for(int i = 0;;i++) {
            if (i != 0) {
                newFilename = prefix + "(" + i + ")" + postfix;
            }

            file = new File("uploads/" + newFilename);
            if (!file.exists()) {
                return file;
            }
        }
    }

    private void sendFinalMessage(String mess) throws IOException {
        byte[] messByte = mess.getBytes(StandardCharsets.UTF_8);
        output.writeInt(messByte.length);
        output.write(messByte);
    }

}