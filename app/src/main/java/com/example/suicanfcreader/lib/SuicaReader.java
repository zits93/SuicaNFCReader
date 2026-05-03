package com.example.suicanfcreader.lib;

import android.util.SparseArray;
import com.example.suicanfcreader.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SuicaReader {

    public int termId;
    public int procId;
    public int year;
    public int month;
    public int day;
    public int kindResId;
    public int remain;
    public int seqNo;
    public int reasion;
    public int inStation;
    public int inLine;
    public int outStation;
    public int outLine;

    public int deviceResId;
    public int actionResId;

    public static final SparseArray<Integer> DEVICE_LIST = new SparseArray<>();
    public static final SparseArray<Integer> ACTION_LIST = new SparseArray<>();

    public SuicaReader(){}

    public static SuicaReader parse(byte[] res, int off) {
        SuicaReader self = new SuicaReader();
        self.init(res, off);
        return self;
    }

    private void init(byte[] res, int off) {
        this.termId = res[off];
        this.procId = res[off+1];
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;

        if (isShopping(this.procId)) {
            this.kindResId = R.string.kind_shopping;
        } else if (isBus(this.procId)) {
            this.kindResId = R.string.kind_bus;
        } else {
            this.kindResId = res[off+6] < 0x80 ? R.string.kind_jr : R.string.kind_public_private ;
        }

        this.inLine = toInt(res, off, 6);         // 6: 出線区
        this.inStation = toInt(res, off, 7);      // 7: 入駅
        this.outLine = toInt(res, off, 8);        // 8: 出線区
        this.outStation = toInt(res, off, 9);     // 9: 出駅
        this.remain  = toInt(res, off, 11,10);    // 10-11: 残高 (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); // 12-14: 連番
        this.reasion = res[off+15];               // 15: リージョン

        this.deviceResId = DEVICE_LIST.get(this.termId, R.string.unknown);
        this.actionResId = ACTION_LIST.get(this.procId, R.string.unknown);
    }

    private int toInt(byte[] res, int off, int... idx) {
        int num = 0;
        for (int j : idx) {
            num = num << 8;
            num += ((int) res[off + j]) & 0x0ff;
        }
        return num;
    }
    private boolean isShopping(int procId) {
        return procId == 70 || procId == 73 || procId == 74 || procId == 75 || procId == 198 || procId == 203;
    }
    private boolean isBus(int procId) {
        return procId == 13|| procId == 15|| procId ==  31|| procId == 35;
    }

    public static byte[] readWithoutEncryption(byte[] idm, int size)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

        bout.write(0);
        bout.write(0x06);
        bout.write(idm);
        bout.write(1);
        bout.write(0x0f);
        bout.write(0x09);
        bout.write(size);
        for (int i = 0; i < size; i++) {
            bout.write(0x80);
            bout.write(i);
        }

        byte[] msg = bout.toByteArray();
        msg[0] = (byte) msg.length;
        return msg;
    }

    static {
        DEVICE_LIST.put(3 , R.string.device_adjustment_machine);
        DEVICE_LIST.put(4 , R.string.device_mobile_terminal);
        DEVICE_LIST.put(5 , R.string.device_onboard_terminal);
        DEVICE_LIST.put(7 , R.string.device_ticket_vending_machine);
        DEVICE_LIST.put(8 , R.string.device_ticket_vending_machine);
        DEVICE_LIST.put(9 , R.string.device_deposit_machine);
        DEVICE_LIST.put(18 , R.string.device_ticket_vending_machine);
        DEVICE_LIST.put(20 , R.string.device_ticket_vending_machine);
        DEVICE_LIST.put(21 , R.string.device_ticket_vending_machine);
        DEVICE_LIST.put(22 , R.string.device_ticket_gate);
        DEVICE_LIST.put(23 , R.string.device_simple_ticket_gate);
        DEVICE_LIST.put(24 , R.string.device_window_terminal);
        DEVICE_LIST.put(25 , R.string.device_window_terminal);
        DEVICE_LIST.put(26 , R.string.device_ticket_gate);
        DEVICE_LIST.put(27 , R.string.device_mobile_phone);
        DEVICE_LIST.put(28 , R.string.device_transfer_adjustment_machine);
        DEVICE_LIST.put(29 , R.string.device_connection_ticket_gate);
        DEVICE_LIST.put(31 , R.string.device_simple_deposit_machine);
        DEVICE_LIST.put(70 , R.string.device_view_altte);
        DEVICE_LIST.put(72 , R.string.device_view_altte);
        DEVICE_LIST.put(199 , R.string.device_shopping_terminal);
        DEVICE_LIST.put(200 , R.string.device_vending_machine);

        ACTION_LIST.put(1 , R.string.action_fare_payment);
        ACTION_LIST.put(2 , R.string.action_charge);
        ACTION_LIST.put(3 , R.string.action_ticket_purchase);
        ACTION_LIST.put(4 , R.string.action_adjustment);
        ACTION_LIST.put(5 , R.string.action_adjustment_in);
        ACTION_LIST.put(6 , R.string.action_window_processing);
        ACTION_LIST.put(7 , R.string.action_new_issuance);
        ACTION_LIST.put(8 , R.string.action_window_deduction);
        ACTION_LIST.put(13 , R.string.action_bus_pitapa);
        ACTION_LIST.put(15 , R.string.action_bus_iruca);
        ACTION_LIST.put(17 , R.string.action_reissuance);
        ACTION_LIST.put(19 , R.string.action_shinkansen);
        ACTION_LIST.put(20 , R.string.action_auto_charge_in);
        ACTION_LIST.put(21 , R.string.action_auto_charge_out);
        ACTION_LIST.put(31 , R.string.action_bus_charge);
        ACTION_LIST.put(35 , R.string.action_bus_plan_ticket);
        ACTION_LIST.put(70 , R.string.action_shopping);
        ACTION_LIST.put(72 , R.string.action_point_charge);
        ACTION_LIST.put(73 , R.string.action_register_deposit);
        ACTION_LIST.put(74 , R.string.action_shopping_cancel);
        ACTION_LIST.put(75 , R.string.action_in_station_shopping);
        ACTION_LIST.put(198 , R.string.action_cash_shopping);
        ACTION_LIST.put(203 , R.string.action_in_station_cash_shopping);
        ACTION_LIST.put(132 , R.string.action_other_adjustment);
        ACTION_LIST.put(133 , R.string.action_other_in_station_adjustment);
    }

}