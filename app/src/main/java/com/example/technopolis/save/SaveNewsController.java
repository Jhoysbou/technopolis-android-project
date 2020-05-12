package com.example.technopolis.save;

import android.content.Context;

import com.example.technopolis.App;
import com.example.technopolis.news.model.NewsItem;
import com.example.technopolis.news.repo.NewsItemRepository;
import com.example.technopolis.news.repo.NewsItemRepositoryImpl;
import com.example.technopolis.news.repo.NewsItemRepositoryImplSubs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveNewsController {
    private static String fileName = "NewsRepoDisk";
    private static String fileNameSub = "NewsSubRepoDisk";

    static void serialize(NewsItemRepository newsItemRepository, NewsItemRepository newsSubItemRepository, App app) throws IOException {
        serializeNews(newsItemRepository, app, fileName);
        serializeNews(newsSubItemRepository, app, fileNameSub);
    }

    //чтение репозитория и основных новостей и подписок, поэтому размер 2
    static boolean read(NewsItemRepository[] newsItemRepositories, App app) throws IOException {
        NewsItemRepository[] newsItemRepositoriesBuf = new NewsItemRepository[1];
        if (newsItemRepositories.length != 2)
            return false;
        newsItemRepositoriesBuf[0] = new NewsItemRepositoryImpl();
        if (!readNews(newsItemRepositoriesBuf, app, fileName)) {
            return false;
        }
        newsItemRepositories[0] = newsItemRepositoriesBuf[0];
        newsItemRepositoriesBuf[0] = new NewsItemRepositoryImplSubs();
        if (!readNews(newsItemRepositoriesBuf, app, fileNameSub)) {
            return false;
        }
        newsItemRepositories[1] = newsItemRepositoriesBuf[0];
        return true;
    }

    private static void serializeNews(NewsItemRepository newsItemRepository, App app, String fileName) throws IOException {
        FileOutputStream writer = app.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
        writer.write(newsItemRepository.findAll().size());
        for (NewsItem item : newsItemRepository.findAll()) {
            writer.write(String.valueOf(item.getId()).getBytes().length);
            writer.write(String.valueOf(item.getId()).getBytes());
            writer.write(item.getName().getBytes().length);
            writer.write(item.getName().getBytes());
            writer.write(item.getTitle().getBytes().length);
            writer.write(item.getTitle().getBytes());
            writer.write(item.getSection().getBytes().length);
            writer.write(item.getSection().getBytes());
            writer.write(item.getDate().getBytes().length);
            writer.write(item.getDate().getBytes());
            writer.write(item.getUserpic().getBytes().length);
            writer.write(item.getUserpic().getBytes());
            writer.write(item.getComments_number().getBytes().length);
            writer.write(item.getComments_number().getBytes());
            writer.write(item.getUrl().getBytes().length);
            writer.write(item.getUrl().getBytes());
        }
        writer.close();
    }

    private static boolean readNews(NewsItemRepository[] newsItemRepository, App app, String fileName) throws IOException {
        FileInputStream reader;
        try {
            reader = app.getApplicationContext().openFileInput(fileName);
        } catch (FileNotFoundException e) {
            return false;
        }
        int size = reader.read();
        for (int i = 0; i < size; i++) {
            int sizeRead = reader.read();
            byte[] buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;
            String temp = new String(buf);
            long id = Long.parseLong(temp);

            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;
            String name = new String(buf);

            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;
            String title = new String(buf);

            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;
            String section = new String(buf);

            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;
            String date = new String(buf);

            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;

            String userpic = new String(buf);
            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;

            String comments_number = new String(buf);
            sizeRead = reader.read();
            buf = new byte[sizeRead];
            if (reader.read(buf) != sizeRead)
                return false;

            String url = new String(buf);
            newsItemRepository[0].add(new NewsItem(id, name, title, section, date, userpic, comments_number, url));
        }
        reader.close();
        return true;
    }

}