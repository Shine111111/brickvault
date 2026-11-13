package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.util.Date;

public class LendingRecordResponseDto {
    private int id;
    private Date startDate;
    private Date endDate;
    private String status;
    private SetInfo legoSet;
    private UserInfo borrower;
    private UserInfo owner;
    private long durationInDays;
    private boolean isDamaged;
    private String damageNotes;
    private int damageSeverity;
    private Date damageAssessmentDate;

    public LendingRecordResponseDto() {
    }

    public LendingRecordResponseDto(int id, Date startDate, Date endDate, String status,
                                    SetInfo legoSet, UserInfo borrower, UserInfo owner, long durationInDays) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.legoSet = legoSet;
        this.borrower = borrower;
        this.owner = owner;
        this.durationInDays = durationInDays;
        this.isDamaged = false;
        this.damageSeverity = 0;
    }

    public LendingRecordResponseDto(int id, Date startDate, Date endDate, String status,
                                    SetInfo legoSet, UserInfo borrower, UserInfo owner, long durationInDays,
                                    boolean isDamaged, String damageNotes, int damageSeverity, Date damageAssessmentDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.legoSet = legoSet;
        this.borrower = borrower;
        this.owner = owner;
        this.durationInDays = durationInDays;
        this.isDamaged = isDamaged;
        this.damageNotes = damageNotes;
        this.damageSeverity = damageSeverity;
        this.damageAssessmentDate = damageAssessmentDate;
    }

    public int getId() { return id; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public SetInfo getLegoSet() { return legoSet; }
    public UserInfo getBorrower() { return borrower; }
    public UserInfo getOwner() { return owner; }
    public long getDurationInDays() { return durationInDays; }
    public boolean isDamaged() { return isDamaged; }
    public String getDamageNotes() { return damageNotes; }
    public int getDamageSeverity() { return damageSeverity; }
    public Date getDamageAssessmentDate() { return damageAssessmentDate; }

    public String getDamageSeverityLabel() {
        return DamageSeverityUtils.getLabelForSeverity(damageSeverity);
    }

    public static class DamageSeverityUtils {
        public static String getLabelForSeverity(int severity) {
            switch (severity) {
                case 0: return "None";
                case 1: return "Minor";
                case 2: return "Moderate";
                case 3: return "Severe";
                default: return "Unknown";
            }
        }
    }

    public static class SetInfo {
        private int id;
        private String setNumber;
        private String name;
        private String theme;
        private String imageUrl;

        public SetInfo() {
        }

        public SetInfo(int id, String setNumber, String name, String theme, String imageUrl) {
            this.id = id;
            this.setNumber = setNumber;
            this.name = name;
            this.theme = theme;
            this.imageUrl = imageUrl;
        }

        public int getId() { return id; }
        public String getSetNumber() { return setNumber; }
        public String getName() { return name; }
        public String getTheme() { return theme; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class UserInfo {
        private int id;
        private String name;
        private String email;

        public UserInfo() {
        }

        public UserInfo(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
