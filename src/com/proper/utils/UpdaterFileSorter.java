package com.proper.utils;

import org.apache.commons.net.ftp.FTPFile;

import java.util.Comparator;

/**
 * Created by Lebel on 10/11/2014.
 * Sorts in an ascending order lower to higher
 */
public class UpdaterFileSorter implements Comparator<FTPFile> {
    @Override
    public int compare(FTPFile lhs, FTPFile rhs) {
        int ret = 0;
        long ld = lhs.getTimestamp().getTime().getTime();
        long rd = rhs.getTimestamp().getTime().getTime();

        if (ld  < rd) {
            ret = -1;
        }else if(ld > rd) {
            ret = 1;
        }else if (ld == rd) {
            ret = 0;
        }
        return ret;
    }
}
