/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author asadj
 */
public class Controller {
    static Statement st=null;
    static ResultSet rs=null;
    Connection con;
    //for db connection
    public Controller(){
        String curnt=System.getProperty("user.dir");
          try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            con=DriverManager.getConnection("jdbc:ucanaccess://"+curnt+"\\ToDo.accdb");
            st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            System.out.println("Connection is Ok");
        }catch(Exception e){System.out.println("--"+e);}
    }
    
    //for user check in loginPage
    public void checkUser(String username,String password,JFrame frame){
        try{
            rs=st.executeQuery("select * from login");
            if(rs.first()){
                if(username.equals(rs.getString(1))){
                    if(password.equals(rs.getString(2))){
                        frame.dispose();
                        new MainFrame().show();
                    }
                    else
                        JOptionPane.showMessageDialog(frame,"Password is wrong","Password",2);
                }
                else
                        JOptionPane.showMessageDialog(frame,"Username is wrong","Username",2);
            }
        }catch(Exception e){System.out.println("--"+e);}
    }
    
    //for modify user in modifyuserpanel
    public void modifyUser(String username,String password,JFrame frame){
        try {
            st.executeUpdate("delete from login");
            st.executeUpdate("Insert into login values('"+username+"','"+password+"')");
            JOptionPane.showMessageDialog(frame,"Password SuccesfullY Changed","Sucessfull!",1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,"Password Not Changed","Not Sucessfull!",0);
        }
    }
    
    //for add designwork in addDesign page
    public void addDesignWork(String clientName,String GivenDate,String DeliverDate,String price,String description,AddDesign dlg){
        try {
            String id="-";
            st.executeUpdate("Insert into designworks (clientname,givendate,deliverdate,price,description)values('"+clientName+"','"+GivenDate+"','"+DeliverDate+"','"+price+"','"+description+"')");
            String task=clientName+","+description;
            rs=st.executeQuery("select id from designworks");
            if(rs.last())
                    id=rs.getString(1);
            st.executeUpdate("Insert into dailytask (task,id2,taskdate)values('"+task+"','"+id+"','"+DeliverDate+"')");
            dlg.clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dlg,"Detail Not Added","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for add printingwork in addPrinting page
    public void addPrintingWork(String vendorName,String givenDate,String receiveDate,String price,String qty,String amount,String description,AddPrinting dlg){
        try {
            st.executeUpdate("Insert into printingworks (vendorname,givendate,receivedate,price,qty,amount,description)values('"+vendorName+"','"+givenDate+"','"+receiveDate+"','"+price+"','"+qty+"','"+amount+"','"+description+"')");
            dlg.clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dlg,"Detail Not Added","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for add vendor in addvendor panel
    public void addVendor(String vendorName,String vendorDetail,MainFrame mf){
        try {
            st.executeUpdate("Insert into vendordetail (vendorname,vendordescription)values('"+vendorName+"','"+vendorDetail+"')");
            mf.clearVendorDetail();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mf,"Detail Not Added","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for fetch daily task in daily task panel
    public void fetchDailyTask(JTable tbl){
        try {
            
            Object col[]={"Id","Id2","No.","Date","Tasks"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select id,id2,taskdate,task from dailytask");
            Object data[]=new Object[5];
            int i=1;
            model.setRowCount(0);
            
            //----------------------
            tbl.getColumnModel().getColumn(0).setMaxWidth(0);
            tbl.getColumnModel().getColumn(0).setMinWidth(0);
            tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
            
            tbl.getColumnModel().getColumn(1).setMaxWidth(0);
            tbl.getColumnModel().getColumn(1).setMinWidth(0);
            tbl.getColumnModel().getColumn(1).setPreferredWidth(0);
            
            tbl.getColumnModel().getColumn(2).setMaxWidth(30);
            tbl.getColumnModel().getColumn(2).setMinWidth(30);
            tbl.getColumnModel().getColumn(2).setPreferredWidth(30);
            
            tbl.getColumnModel().getColumn(3).setMaxWidth(100);
            tbl.getColumnModel().getColumn(3).setMinWidth(100);
            tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
            
            tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            
            //-------------------------------------------
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=rs.getString(2);
                data[2]=i++;
                SimpleDateFormat date=new SimpleDateFormat("dd-MMM-yyyy");
                data[3]=date.format(rs.getDate(3));
                data[4]=rs.getString(4);
                model.addRow(data);
            }   
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(tbl,"Daily Task not fetched in list","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for fetch vendor in addprinting page
    public void fetchVendors(JComboBox cb){
        try {
            rs=st.executeQuery("select vendorname from vendordetail");
            while(rs.next()){
                cb.addItem(rs.getObject(1));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cb,"Vendor name not fetched in list","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for fetch pending works in deshboard
    public void fetchDeshboard(JLabel pd,JLabel pp,JLabel dt,JLabel lblDailyTask){
        try {
            rs=st.executeQuery("select clientname from designworks");
            rs.last();
            int no=rs.getRow();
            if(no<10)
                pd.setText("0"+Integer.toString(no));
            else
                pd.setText(Integer.toString(no));
            
            rs=st.executeQuery("select vendorname from printingworks");
            rs.last();
            no=rs.getRow();
            if(no<10)
                pp.setText("0"+Integer.toString(no));
            else
                pp.setText(Integer.toString(no));
            
            rs=st.executeQuery("select task from dailytask");
            rs.last();
            no=rs.getRow();
            if(no<10)
                lblDailyTask.setText("0"+Integer.toString(no));
            else
                lblDailyTask.setText(Integer.toString(no));
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(pd,"Detail Not Added","Not Sucessfull!",0);
            System.out.print(ex);
        }
        
    }
    
    //for fetch pending design work in designworks panel
    public void fetchPendingDesign(JTable tbl){
        try{
            Object col[]={"Key","No.","Client Name","Given Date","Deliver Date","Price","Work Detail"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select id,clientname,givendate,deliverdate,price,description from designworks");
            Object data[]=new Object[7];
            int i=1;
            model.setRowCount(0);
            //---------------------
                tbl.getColumnModel().getColumn(0).setMaxWidth(0);
                tbl.getColumnModel().getColumn(0).setMinWidth(0);
                tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
                tbl.getColumnModel().getColumn(1).setMaxWidth(30);
                tbl.getColumnModel().getColumn(1).setMinWidth(30);
                tbl.getColumnModel().getColumn(1).setPreferredWidth(30);
                
                tbl.getColumnModel().getColumn(3).setMaxWidth(100);
                tbl.getColumnModel().getColumn(3).setMinWidth(100);
                tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(4).setMaxWidth(100);
                tbl.getColumnModel().getColumn(4).setMinWidth(100);
                tbl.getColumnModel().getColumn(4).setPreferredWidth(100);
               
                tbl.getColumnModel().getColumn(5).setMaxWidth(100);
                tbl.getColumnModel().getColumn(5).setMinWidth(100);
                tbl.getColumnModel().getColumn(5).setPreferredWidth(100);
                
                tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            //----------------------------
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=i++;
                data[2]=rs.getString(2);
                data[3]=rs.getString(3);
                data[4]=rs.getString(4);
                data[5]=rs.getString(5);
                data[6]=rs.getString(6);
                model.addRow(data);
            }
        }catch(Exception e){
            System.out.println(""+e);
        }
        
    }
    
    //for fetch finished design work in designworks panel
    public void fetchFinishedDesign(JTable tbl){
        try{
            Object col[]={"Key","No.","Client Name","Given Date","Deliver Date","Price","Work Detail"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select id,clientname,givendate,deliverdate,price,description from finisheddesignworks");
            Object data[]=new Object[7];
            int i=1;
            model.setRowCount(0);
            //---------------------
                tbl.getColumnModel().getColumn(0).setMaxWidth(0);
                tbl.getColumnModel().getColumn(0).setMinWidth(0);
                tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
                tbl.getColumnModel().getColumn(1).setMaxWidth(30);
                tbl.getColumnModel().getColumn(1).setMinWidth(30);
                tbl.getColumnModel().getColumn(1).setPreferredWidth(30);
                
                tbl.getColumnModel().getColumn(3).setMaxWidth(100);
                tbl.getColumnModel().getColumn(3).setMinWidth(100);
                tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(4).setMaxWidth(100);
                tbl.getColumnModel().getColumn(4).setMinWidth(100);
                tbl.getColumnModel().getColumn(4).setPreferredWidth(100);
               
                tbl.getColumnModel().getColumn(5).setMaxWidth(100);
                tbl.getColumnModel().getColumn(5).setMinWidth(100);
                tbl.getColumnModel().getColumn(5).setPreferredWidth(100);
                
                tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            //----------------------------
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=i++;
                data[2]=rs.getString(2);
                data[3]=rs.getString(3);
                data[4]=rs.getString(4);
                data[5]=rs.getString(5);
                data[6]=rs.getString(6);
                model.addRow(data);
            }
        }catch(Exception e){
            System.out.println(""+e);
        }
        
    }
    
    //for fetch pending printing work in Printingworks panel
    public void fetchPendingPrinting(JTable tbl){
        try{
            Object col[]={"Key","No.","Vendor Name","Price","Qty","Amount","Given Date","Deliver Date","Work Detail"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select * from printingworks");
            Object data[]=new Object[9];
            int i=1;
            model.setRowCount(0);
            //---------------------
                tbl.getColumnModel().getColumn(0).setMaxWidth(0);
                tbl.getColumnModel().getColumn(0).setMinWidth(0);
                tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
                tbl.getColumnModel().getColumn(1).setMaxWidth(30);
                tbl.getColumnModel().getColumn(1).setMinWidth(30);
                tbl.getColumnModel().getColumn(1).setPreferredWidth(30);
                
                tbl.getColumnModel().getColumn(3).setMaxWidth(100);
                tbl.getColumnModel().getColumn(3).setMinWidth(100);
                tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(4).setMaxWidth(100);
                tbl.getColumnModel().getColumn(4).setMinWidth(100);
                tbl.getColumnModel().getColumn(4).setPreferredWidth(100);
               
                tbl.getColumnModel().getColumn(5).setMaxWidth(100);
                tbl.getColumnModel().getColumn(5).setMinWidth(100);
                tbl.getColumnModel().getColumn(5).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(6).setMaxWidth(100);
                tbl.getColumnModel().getColumn(6).setMinWidth(100);
                tbl.getColumnModel().getColumn(6).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(7).setMaxWidth(100);
                tbl.getColumnModel().getColumn(7).setMinWidth(100);
                tbl.getColumnModel().getColumn(7).setPreferredWidth(100);
                
                tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            //----------------------------
            
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=i++;
                data[2]=rs.getString(2);
                data[3]=rs.getString(5);
                data[4]=rs.getString(6);
                data[5]=rs.getString(7);
                data[6]=rs.getString(3);
                data[7]=rs.getString(4);
                data[8]=rs.getString(8);
                model.addRow(data);
            }
        }catch(Exception e){
            System.out.println(""+e);
        }
        
    }
    
    //for fetch finished printing work in Printingworks panel
    public void fetchFinishedPrinting(JTable tbl){
        try{
            Object col[]={"Key","No.","Vendor Name","Price","Qty","Amount","Given Date","Deliver Date","Work Detail"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select * from finishedprintingworks");
            Object data[]=new Object[9];
            int i=1;
            model.setRowCount(0);
            //---------------------
                tbl.getColumnModel().getColumn(0).setMaxWidth(0);
                tbl.getColumnModel().getColumn(0).setMinWidth(0);
                tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
                tbl.getColumnModel().getColumn(1).setMaxWidth(30);
                tbl.getColumnModel().getColumn(1).setMinWidth(30);
                tbl.getColumnModel().getColumn(1).setPreferredWidth(30);
                
                tbl.getColumnModel().getColumn(3).setMaxWidth(100);
                tbl.getColumnModel().getColumn(3).setMinWidth(100);
                tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(4).setMaxWidth(100);
                tbl.getColumnModel().getColumn(4).setMinWidth(100);
                tbl.getColumnModel().getColumn(4).setPreferredWidth(100);
               
                tbl.getColumnModel().getColumn(5).setMaxWidth(100);
                tbl.getColumnModel().getColumn(5).setMinWidth(100);
                tbl.getColumnModel().getColumn(5).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(6).setMaxWidth(100);
                tbl.getColumnModel().getColumn(6).setMinWidth(100);
                tbl.getColumnModel().getColumn(6).setPreferredWidth(100);
                
                tbl.getColumnModel().getColumn(7).setMaxWidth(100);
                tbl.getColumnModel().getColumn(7).setMinWidth(100);
                tbl.getColumnModel().getColumn(7).setPreferredWidth(100);
                
                tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            //----------------------------
            
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=i++;
                data[2]=rs.getString(2);
                data[3]=rs.getString(5);
                data[4]=rs.getString(6);
                data[5]=rs.getString(7);
                data[6]=rs.getString(3);
                data[7]=rs.getString(4);
                data[8]=rs.getString(8);
                model.addRow(data);
            }
        }catch(Exception e){
            System.out.println(""+e);
        }
        
    }
    
    //for fetch vendor in addVendor panel
    public void fetchVendorsDetail(JTable tbl){
        try {
            
            Object col[]={"Key","No.","Vendor Name","Vendor Detail"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select * from vendordetail");
            Object data[]=new Object[4];
            int i=1;
            model.setRowCount(0);
            
            //----------------------
            tbl.getColumnModel().getColumn(0).setMaxWidth(0);
            tbl.getColumnModel().getColumn(0).setMinWidth(0);
            tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
            
            tbl.getColumnModel().getColumn(1).setMaxWidth(30);
            tbl.getColumnModel().getColumn(1).setMinWidth(30);
            tbl.getColumnModel().getColumn(1).setPreferredWidth(30);
            
            tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            
            //-------------------------------------------
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=i++;
                data[2]=rs.getString(2);
                data[3]=rs.getString(3);
                model.addRow(data);
            }   
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(tbl,"Vendor details not fetched in list","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }

    //for delete vendor in addVendor panel
    public void deleteVendor(String vendorNo){
        try {
            st.executeUpdate("delete from vendordetail where id="+vendorNo);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for delete design in designwork panel pending
    public void deletePendingDesign(String designWorksNo){
        try {
            st.executeUpdate("delete from designworks where id="+designWorksNo);
            st.executeUpdate("delete from dailytask where id2='"+designWorksNo+"'");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for delete design in designwork panel finished
    public void deleteFinishedDesign(String designWorksNo){
        try {
            st.executeUpdate("delete from finisheddesignworks where id="+designWorksNo);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //fro delete printing in printingwork panel pending
    public void deletePendingPrinting(String printingWorksNo){
        try {
            st.executeUpdate("delete from printingworks where id="+printingWorksNo);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    //fro delete printing in printingwork panel pending
    public void deleteFinishedPrinting(String printingWorksNo){
        try {
            st.executeUpdate("delete from finishedprintingworks where id="+printingWorksNo);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for update designwork in addDesign panel
    public void updateDesignWork(String clientName,String GivenDate,String DeliverDate,String price,String description,AddDesign dlg,String id){
        try {
            st.executeUpdate("update designworks set clientname='"+clientName+"',givendate='"+GivenDate+"',deliverdate='"+DeliverDate+"',price='"+price+"',description='"+description+"' where id="+id);
            dlg.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dlg,"Detail Not Added","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //for add printingwork in addPrinting page
    public void updatePrintingWork(String vendorName,String givenDate,String receiveDate,String price,String qty,String amount,String description,AddPrinting dlg,String id){
        try {
            st.executeUpdate("update printingworks set vendorname='"+vendorName+"',givendate='"+givenDate+"',receivedate='"+receiveDate+"',price='"+price+"',qty='"+qty+"',amount='"+amount+"',description='"+description+"' where id="+id);
            dlg.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dlg,"Detail Not Updated","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
   
    //for finish design in designwork panel pending
    public void finishPendingDesign(String designWorksNo,Object obj[]){
        try {
            st.executeUpdate("delete from designworks where id="+designWorksNo);
            st.executeUpdate("Insert into finisheddesignworks (clientname,givendate,deliverdate,price,description)values('"+obj[0]+"','"+obj[1]+"','"+obj[2]+"','"+obj[3]+"','"+obj[4]+"')");
            st.executeUpdate("delete from dailytask where id2='"+designWorksNo+"'");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for unfinish design in designwork panel pending
    public void unFinishPendingDesign(String designWorksNo,Object obj[]){
        try {
            String id="-";
            st.executeUpdate("delete from finisheddesignworks where id="+designWorksNo);
            st.executeUpdate("Insert into designworks (clientname,givendate,deliverdate,price,description)values('"+obj[0]+"','"+obj[1]+"','"+obj[2]+"','"+obj[3]+"','"+obj[4]+"')");
            String task=obj[0]+","+obj[4];
            rs=st.executeQuery("select id from designworks");
            if(rs.last())
                    id=rs.getString(1);
            st.executeUpdate("Insert into dailytask (task,id2,taskdate)values('"+task+"','"+id+"','"+obj[2]+"')");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //fro finish printing in printingwork panel pending
    public void finishPendingPrinting(String printingWorksNo,Object obj[]){
        try {
            st.executeUpdate("delete from printingworks where id="+printingWorksNo);
            st.executeUpdate("Insert into finishedprintingworks (vendorname,givendate,receivedate,price,qty,amount,description)values('"+obj[0]+"','"+obj[1]+"','"+obj[2]+"','"+obj[3]+"','"+obj[4]+"','"+obj[5]+"','"+obj[6]+"')");
            } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //fro unfinish printing in printingwork panel pending
    public void unFinishPendingPrinting(String printingWorksNo,Object obj[]){
        try {
            st.executeUpdate("delete from finishedprintingworks where id="+printingWorksNo);
            st.executeUpdate("Insert into printingworks (vendorname,givendate,receivedate,price,qty,amount,description)values('"+obj[0]+"','"+obj[1]+"','"+obj[2]+"','"+obj[3]+"','"+obj[4]+"','"+obj[5]+"','"+obj[6]+"')");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for add daily task
    public void addDailyTask(String task,String d){
        try {
            st.executeUpdate("Insert into dailytask (task,id2,taskdate)values('"+task+"','-','"+d+"')");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for finish daily task
    public void finishDailyTask(String taskID){
        try {
            st.executeUpdate("delete from dailytask where id='"+taskID+"'");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for finish daily task
    public void clearDailyTask(String d){
        try {
            st.executeUpdate("delete from dailytask where id2='-' and taskdate='"+d+"'");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    
    //for user check in loginPage
    public void checkUserForReset(String username,String password,MainFrame frame,JPanel p,JButton b){
        try{
            rs=st.executeQuery("select * from login");
            if(rs.first()){
                if(username.equals(rs.getString(1))){
                    if(password.equals(rs.getString(2))){
                        clearDB();
                        JOptionPane.showMessageDialog(frame,"Softwere is Reseted","reset",1);
                        frame.hidePanel();
                        frame.switchPanel(p,b);
                    }
                    else
                        JOptionPane.showMessageDialog(frame,"Password is wrong","Password",2);
                }
                else
                        JOptionPane.showMessageDialog(frame,"Username is wrong","Username",2);
            }
        }catch(Exception e){System.out.println("--"+e);}
    }
    
    //for fetch Specific day task in daily tas panel
    public void fetchPerticularDayTask(String d,JTable tbl){
        try {
            
            Object col[]={"Id","Id2","No.","Date","Tasks"};
            DefaultTableModel model=(DefaultTableModel) tbl.getModel();
            model.setColumnIdentifiers(col);
            tbl.setModel(model);
            tbl.setDefaultEditor(Object.class,null);
            rs=st.executeQuery("select id,id2,taskdate,task from dailytask where taskdate='"+d+"'");
            Object data[]=new Object[5];
            int i=1;
            model.setRowCount(0);
            
            //----------------------
            tbl.getColumnModel().getColumn(0).setMaxWidth(0);
            tbl.getColumnModel().getColumn(0).setMinWidth(0);
            tbl.getColumnModel().getColumn(0).setPreferredWidth(0);
            
            tbl.getColumnModel().getColumn(1).setMaxWidth(0);
            tbl.getColumnModel().getColumn(1).setMinWidth(0);
            tbl.getColumnModel().getColumn(1).setPreferredWidth(0);
            
            tbl.getColumnModel().getColumn(2).setMaxWidth(30);
            tbl.getColumnModel().getColumn(2).setMinWidth(30);
            tbl.getColumnModel().getColumn(2).setPreferredWidth(30);
            
            tbl.getColumnModel().getColumn(3).setMaxWidth(100);
            tbl.getColumnModel().getColumn(3).setMinWidth(100);
            tbl.getColumnModel().getColumn(3).setPreferredWidth(100);
            
            tbl.setAutoResizeMode(tbl.AUTO_RESIZE_LAST_COLUMN);
            
            //-------------------------------------------
            while(rs.next()){
                data[0]=rs.getString(1);
                data[1]=rs.getString(2);
                data[2]=i++;
                SimpleDateFormat date=new SimpleDateFormat("dd-MMM-yyyy");
                data[3]=date.format(rs.getDate(3));
                data[4]=rs.getString(4);
                model.addRow(data);
            }   
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(tbl,"Daily Task not fetched in list","Not Sucessfull!",0);
            System.out.print(ex);
        }
    }
    
    //fetch notes in dashboard
    public void fetchNotes(JList listNotes, String date) {
        try {
            DefaultListModel model=new DefaultListModel();
            listNotes.setModel(model);
            rs=st.executeQuery("select task from dailytask where taskdate='"+date+"'");
            int i=1;
            while(rs.next()){
                model.addElement(i+++" - "+rs.getString(1));
            }
            if(i==1)
                model.addElement("There is not any task available on this day");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //fetch dates in dashboard
    public void fetchDates(JTextArea listDates, String date) {
        try {
            String d1="01-"+date,d2="31-"+date;
            rs=st.executeQuery("select DISTINCT taskdate from dailytask where taskdate BETWEEN '"+d1+"' AND '"+d2+"'");
            int i=0;
            SimpleDateFormat d=new SimpleDateFormat("dd");
            while(rs.next()){
                listDates.append(d.format(rs.getDate(1))+",");
                i++;
                if(i==10)
                    listDates.append("\n");
            }
            if(i==0)
                listDates.setText("Not Available");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    //for detabase clearation
    public void clearDB(){
        try {
            st.executeUpdate("Delete from dailytask");
            st.executeUpdate("Delete from designworks");
            st.executeUpdate("Delete from finisheddesignworks");
            st.executeUpdate("Delete from finishedprintingworks");
            st.executeUpdate("Delete from printingworks");
            st.executeUpdate("Delete from vendordetail");
            System.out.println("Database Cleared");
        } catch (Exception ex) {
            
            System.out.print(ex);
        }
    }
    
    public static void main(String[] args) {
        new Controller().clearDB();
    }

    
}
