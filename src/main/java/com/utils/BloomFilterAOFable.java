package com.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.BitSet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 布隆过滤器
 * 支持AOF持久化
 * 通过配置MAX_PIECE_AOF_QUEUE参数调整持久化的频率
 * @param <E>  布隆判断的数据类型
 */
public class BloomFilterAOFable<E> extends BloomFilter {

    /* 日志工具 */
    private static final Logger logger =
            LoggerFactory.getLogger(BloomFilterAOFable.class);

    /* 当队列中消息大于该数值时 启动AOF备份 */
    private int MAX_PIECE_AOF_QUEUE;

    /* 阻塞队列，存放命令 */
    private LinkedBlockingQueue<E> blockingQueue;

    /* 备份文件的路径 */
    private String backupFilePath;

    /* 备份文件线程对象 */
    private WriterRunnable writerRunnable;

    /* 执行写入操作的线程 */
    private Thread writeThread;

    public BloomFilterAOFable(double c, int n, int k, String backupFilePath,
                               boolean restoreFromFile) {
        super(c, n, k);
        // 恢复数据 初始化阻塞队列 初始化写线程
        blockingQueue = new LinkedBlockingQueue<E>();
        this.writerRunnable = new WriterRunnable<E>(
                backupFilePath, blockingQueue, MAX_PIECE_AOF_QUEUE
        );
        this.writeThread = new Thread(this.writerRunnable);
        this.writeThread.start();
        if(restoreFromFile){
            try {
                restore();
            } catch (Exception e) {
                logger.warn("restore failed...");
            }
        }

    }

    public BloomFilterAOFable(int bitSetSize, int expectedNumberOElements,
            String backupFilePath, int MAX_PIECE_AOF_QUEUE) {
        super(bitSetSize, expectedNumberOElements);
        this.MAX_PIECE_AOF_QUEUE = MAX_PIECE_AOF_QUEUE;
        this.blockingQueue = new LinkedBlockingQueue<E>();
        this.writerRunnable = new WriterRunnable<E>(
                backupFilePath, blockingQueue, MAX_PIECE_AOF_QUEUE
        );
        this.writeThread = new Thread(this.writerRunnable);
        this.writeThread.start();
    }

    public BloomFilterAOFable(double falsePositiveProbability,
              int expectedNumberOfElements, String backupFilePath,
               boolean restoreFromFile, int MAX_PIECE_AOF_QUEUE) {
        super(falsePositiveProbability, expectedNumberOfElements);
        this.MAX_PIECE_AOF_QUEUE = MAX_PIECE_AOF_QUEUE;
        this.backupFilePath = backupFilePath;
        blockingQueue = new LinkedBlockingQueue<E>();
        this.writerRunnable = new WriterRunnable<E>(
                backupFilePath, blockingQueue, MAX_PIECE_AOF_QUEUE
        );
        this.writeThread = new Thread(this.writerRunnable);
        this.writeThread.setName("BloomWriter");
        this.writeThread.start();
        if(restoreFromFile){
            try {
                restore();
            } catch (IOException e) {
                logger.warn("restore failed...");
            }
        }
    }

    public BloomFilterAOFable(int bitSetSize, int expectedNumberOfFilterElements,
              int actualNumberOfFilterElements, BitSet filterData,
              String backupFilePath) {
        super(bitSetSize, expectedNumberOfFilterElements,
                actualNumberOfFilterElements, filterData);
        blockingQueue = new LinkedBlockingQueue<E>();
        this.writerRunnable = new WriterRunnable<E>(
                backupFilePath, blockingQueue, MAX_PIECE_AOF_QUEUE
        );
        this.writeThread = new Thread(this.writerRunnable);
        this.writeThread.start();
    }


    /**
     * 重写父类中的add
     * 在每次向布隆插入一个数据之后，会将对应的hashCode放入阻塞队列中
     * @param bytes array of bytes to add to the Bloom filter.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void add(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
            int a = Math.abs(hash % bitSetSize);
            sb.append(a);
            sb.append("_");
            bitset.set(a, true);
        }
        numberOfAddedElements++;
        logger.info(sb.toString());
        // 队列中插入数据对应的所有HashCode组成的字符串
        blockingQueue.offer((E)sb.toString());
    }

    /**
     * 从备份文件中恢复数据
     * @throws IOException
     */
    private void restore() throws IOException {
        File file = new File(backupFilePath);
        FileReader fd;
        BufferedReader bf = null;
        try {
            fd = new FileReader(file);
            bf = new BufferedReader(fd);
        } catch (FileNotFoundException e) {
            logger.warn("dbFile not exist, out...");
        }
        logger.info("restoring from file: {}", backupFilePath);
        String nextLine;
        while(bf != null && (nextLine = bf.readLine()) != null){
            String[] hashCodes = nextLine.split("_");
            for(String hash : hashCodes){
                if(!hash.equals("")){
                    super.bitset.set(Integer.valueOf(hash));
                }
            }
        }
        logger.info("restore success...");
    }


    /**
     * 备份线程
     * @param <T>
     */
    static class WriterRunnable<T> implements Runnable{

        /* 总共保存的次数 */
        static int allSavedTimes = 0;

        /* 文件的路径 */
        String backupFilePath;

        /* 队列 */
        LinkedBlockingQueue<T> blockingQueue;

        /* 最大数目 */
        int maxPieceAOFQueue;

        WriterRunnable(String backupFilePath, LinkedBlockingQueue<T> blockingQueue,
                       int maxPieceAOFQueue){
            this.backupFilePath = backupFilePath;
            this.blockingQueue = blockingQueue;
            this.maxPieceAOFQueue = maxPieceAOFQueue;
        }

        public static int getAllSavedTimes(){
            return allSavedTimes;
        }


        /**
         * 备份数据的线程
         */
        @Override
        public void run() {
            // 打开文件
            logger.info("Writer Thread Start...");
            File file = new File(backupFilePath);
            FileWriter fw = null;
            PrintWriter pw = null;
            if(!file.exists()){
                try {
                    // 文件不存在则创建文件
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while(true){
                int count = 0;
                try {
                    fw = new FileWriter(file, true);
                    pw = new PrintWriter(fw);
                    if(blockingQueue.size() < maxPieceAOFQueue){
                        continue;
                    }
                    while(count < maxPieceAOFQueue){
                        pw.println(blockingQueue.poll());
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(pw != null){
                        pw.close();
                    }
                    try {
                        if(fw != null) {
                            fw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            allSavedTimes++;
            logger.info("saved success for {} times...", allSavedTimes);
            }
        }
    }
}

