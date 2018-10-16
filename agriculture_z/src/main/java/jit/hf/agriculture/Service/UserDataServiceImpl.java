package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.UserDataRepository;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.domain.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Calendar;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午5:35 18-6-3
 **/
@Service
public class UserDataServiceImpl implements UserDataService {

    @Autowired
    UserDataRepository userDataRepository;

    @Override
    public void editUserData(User user) {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
                }
            userData = new UserData();
            userData.setDate(date);
            userData.setUsers(userData.getUsers() + "," + user.getId());
            userData.setNumbers(userData.getNumbers() + 1);
        } else {
            String rs=userData.getUsers();
            if(rs.indexOf(user.getId().toString()) == -1) {
                userData.setNumbers(userData.getNumbers() + 1);
                userData.setUsers(userData.getUsers() + "," + user.getId());
            }
        }

        userDataRepository.save(userData);

    }

    @Override
    public void editDocData() {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
            }
            userData = new UserData();
            userData.setDate(date);
            userData.setDocDownLoad(userData.getDocDownLoad() + 1);
        } else {
            userData.setDocDownLoad(userData.getDocDownLoad() + 1);
        }
        userDataRepository.save(userData);
    }

    @Override
    public void editTalkData() {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
            }
            userData = new UserData();
            userData.setDate(date);
            userData.setTalkComments(userData.getTalkComments() + 1);
        } else {
            userData.setTalkComments(userData.getTalkComments() + 1);
        }
        userDataRepository.save(userData);
    }

    @Override
    public void editChoiceData() {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
            }
            userData = new UserData();
            userData.setDate(date);
            userData.setChoiceClicks(userData.getChoiceClicks() + 1);
        } else {
            userData.setChoiceClicks(userData.getChoiceClicks() + 1);
        }
        userDataRepository.save(userData);
    }

    @Override
    public void editVideoClicks() {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
            }
            userData = new UserData();
            userData.setDate(date);
            userData.setVideoClicks(userData.getVideoClicks() + 1);
        } else {
            userData.setVideoClicks(userData.getVideoClicks() + 1);
        }
        userDataRepository.save(userData);
    }

    @Override
    public void editVideoComments() {
        String date=dateTime();
        UserData userData=userDataRepository.findByDate(date);
        if(userData==null) {
            if(userDataRepository.count()>=31) {
                String b=beforeTime();
                UserData userData1=userDataRepository.findByDate(b);
                userDataRepository.delete(userData1);
            }
            userData = new UserData();
            userData.setDate(date);
            userData.setVideoComments(userData.getVideoComments() + 1);
        } else {
            userData.setVideoComments(userData.getVideoComments() + 1);
        }
        userDataRepository.save(userData);
    }

    //获取当前日期
    public static String dateTime() {
        Calendar cal = Calendar.getInstance();
        String date, day, month, year;
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        day = String.valueOf(cal.get(Calendar.DATE));
        date = year + "-" + month + "-" + day;
        return date;
    }

    //获取30天前日期
    public static String beforeTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.DATE, -31);//最后一个数字31可改，31天的意思
        String date, day, month, year;
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        day = String.valueOf(cal.get(Calendar.DATE));
        date = year + "-" + month + "-" + day;
        return date;
    }
}
