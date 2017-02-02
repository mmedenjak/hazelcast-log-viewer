package com.hazelcast.logreader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.hazelcast.logreader.DirectionType.DOWN;
import static com.hazelcast.logreader.DirectionType.UP;

public class RandomDirectionBufferedFileReader implements Closeable {
    private static final int MAX_LINE_BYTES = 1024 * 1024;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    private static final char LF = '\n', CR = '\r';
    private final byte[] buffer; // for fetching batches of chars from the file
    private final long fileLength;
    private final RandomAccessFile in;
    private final byte[] lineBuffer; // for holding a complete line, created from the byte buffer
    private long filePosition;
    private ReadStrategy strategy;


    public RandomDirectionBufferedFileReader(File file) throws IOException {
        this(file, DEFAULT_BUFFER_SIZE, MAX_LINE_BYTES);
    }

    public RandomDirectionBufferedFileReader(File file, int bufferSize, int maxLineBytes) throws IOException {
        this.in = new RandomAccessFile(file, "r");
        this.filePosition = 0;
        in.seek(filePosition);
        this.strategy = new ForwardReadStrategy();
        this.buffer = new byte[bufferSize];
        this.lineBuffer = new byte[maxLineBytes];
        this.fileLength = in.length();
    }

    public DirectionType getDirection() {
        return strategy.getDirectionType();
    }

    public void jumpToEnd(DirectionType type) throws IOException {
        switch (type) {
            case DOWN:
                this.filePosition = in.length() - 1;
                break;
            case UP:
                this.filePosition = 0;
                break;
        }
        in.seek(filePosition);
    }

    public void switchDirection() {
        setDirection(getDirection() == DOWN ? UP : DOWN);
    }

    public void setDirection(DirectionType direction) {
        if (strategy.getDirectionType() != direction) {
            strategy = direction == DOWN ? new ForwardReadStrategy() : new BackwardReadStrategy();
        }
    }

    public String readLine() throws IOException {
        return strategy.readLine();
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
        }
    }


    private interface ReadStrategy {
        DirectionType getDirectionType();

        String readLine() throws IOException;
    }

    private class ForwardReadStrategy implements ReadStrategy {
        private int bufferEndPosition = -1, lineBufferEndPosition = -1, bufferPosition = 0;
        private boolean lineBuffered = false;

        @Override
        public String readLine() throws IOException {
            if (filePosition >= fileLength) {
                return null;
            }
            if (!lineBuffered) {
                fillLineBuffer();
            }
            if (lineBuffered) {
                lineBuffered = false;
                return new String(lineBuffer, 0, lineBufferEndPosition);
            } else {
                throw new RuntimeException("WOOT"); // line should be buffered here or exception should have been thrown
            }
        }

        private void fillLineBuffer() throws IOException {
            lineBufferEndPosition = 0;
            while (true) {
                if (bufferEndPosition < 0 || bufferPosition >= bufferEndPosition) {
                    fillBuffer();

                    // nothing was buffered - we reached the end of a file
                    if (bufferEndPosition < 0 || bufferPosition >= bufferEndPosition) {
                        break;
                    }
                }

                byte byt = buffer[bufferPosition++];
                filePosition++;
                if (byt == LF) {
                    break;
                }
                if (byt == CR) {
                    // just ignore \r for now
                    continue;
                }
                if (lineBufferEndPosition == lineBuffer.length) {
                    throw new IOException("file has a line exceeding " + lineBuffer.length
                            + " bytes; use constructor to pickup bigger line buffer");
                }
                lineBuffer[lineBufferEndPosition++] = byt;
            }
            lineBuffered = true;
        }

        private void fillBuffer() throws IOException {
            if (filePosition >= fileLength) {
                return;
            }
            in.seek(filePosition);
            this.bufferEndPosition = in.read(buffer);
            this.bufferPosition = 0;
            if (bufferEndPosition >= 0 && buffer[bufferPosition] == LF) {
                bufferPosition++;
                filePosition++;
            }
        }

        @Override
        public DirectionType getDirectionType() {
            return DOWN;
        }
    }

    private class BackwardReadStrategy implements ReadStrategy {
        private int lineBufferEndPosition = -1, bufferPosition = -1;
        private boolean lineBuffered = false;

        @Override
        public String readLine() throws IOException {
            if (filePosition <= 0) {
                return null;
            }
            if (!lineBuffered) {
                fillLineBuffer();
            }
            if (lineBuffered) {
                lineBuffered = false;
                return new String(lineBuffer, 0, lineBufferEndPosition);
            } else {
                throw new RuntimeException("WOOT"); // line should be buffered here or exception should have been thrown
            }
        }

        @Override
        public DirectionType getDirectionType() {
            return UP;
        }

        private void fillBuffer() throws IOException {
            if (filePosition <= 0) {
                return;
            }

            if (filePosition < buffer.length) {
                in.seek(0);
                in.read(buffer, 0, (int) filePosition);
                bufferPosition = (int) filePosition - 1;
            } else {
                in.seek(filePosition - buffer.length);
                in.read(buffer);
                bufferPosition = buffer.length - 1;
            }
            if (bufferPosition >= 0 && buffer[bufferPosition] == LF) {
                bufferPosition--;
                filePosition--;
            }
        }

        private void fillLineBuffer() throws IOException {
            lineBufferEndPosition = 0;

            while (true) {
                if (bufferPosition < 0) {
                    fillBuffer();

                    // nothing was buffered - we reached the beginning of a file
                    if (bufferPosition < 0) {
                        break;
                    }
                }

                byte byt = buffer[bufferPosition--];
                filePosition--;
                if (byt == LF) {
                    break;
                }
                if (byt == CR) {
                    // just ignore \r for now
                    continue;
                }
                if (lineBufferEndPosition == lineBuffer.length) {
                    throw new IOException("file has a line exceeding " + lineBuffer.length
                            + " bytes; use constructor to pickup bigger line buffer");
                }
                lineBuffer[lineBufferEndPosition++] = byt;
            }
            reverse(lineBuffer, lineBufferEndPosition);
            lineBuffered = true;
        }

        private byte[] reverse(byte[] a, int end) {
            if (end < 2) {
                return a;
            }
            for (int i = 0; i < end / 2; i++) {
                byte temp = a[i];
                a[i] = a[end - i - 1];
                a[end - i - 1] = temp;
            }
            return a;
        }
    }
}