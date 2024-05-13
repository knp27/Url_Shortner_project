package in.co.codeplanet.urlshortner.bean;

public class EmailDetails {

        private String recipient;
        private String subject;
        private String body;

        public EmailDetails(String recipient, String subject, String body) {
            this.recipient = recipient;
            this.subject = subject;
            this.body = body;
        }


        public String getRecipient() {
            return recipient;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setBody(String body) {
            this.body = body;
        }


}


