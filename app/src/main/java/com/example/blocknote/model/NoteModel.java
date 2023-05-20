package com.example.blocknote.model;

public class NoteModel {
   String title, content, share, image;

   public NoteModel(){}

   public NoteModel(String title, String content, String share, String image) {
      this.title = title;
      this.content = content;
      this.share = share;
      this.image = image;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getShare() {
      return share;
   }

   public void setShare(String share) {
      this.share = share;
   }

   public String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
   }
}
