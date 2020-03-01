import 'package:flutter/material.dart';

import 'dart:ui';

import 'package:flutter/painting.dart';

void main() => runApp(MyApp(initParams: window.defaultRouteName));

class MyApp extends StatelessWidget {
  final String initParams;

  MyApp({Key key, this.initParams}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
              title: Align(
            alignment: Alignment.center,
            child: Text("问答"),
          )),
          body: ListView(
            children: <Widget>[
              ListItem(),
              ListItem(),
              ListItem(),
            ],
          )),
    );
  }
}

class ListItem extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.fromLTRB(0, 10, 0, 0),
      child: Row(children: <Widget>[
        Padding(
            padding: EdgeInsets.all(10.0),
            child: Image.asset(
              "assets/image/timeline_like_normal.png",
              width: 15.0,
              height: 15.0,
            )),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Padding(
              padding: EdgeInsets.fromLTRB(0, 0, 0, 6),
              child: Text(
                "测试文字",
                textAlign: TextAlign.left,
                style: TextStyle(fontSize: 14.0, color: Color(0xff000000)),
              ),
            ),
            Row(children: <Widget>[
              Padding(
                  padding: EdgeInsets.fromLTRB(0, 0, 8, 0),
                  child: Container(
                      decoration: BoxDecoration(
                          border: Border.all(
                              color: Color.fromARGB(255, 7, 157, 101),
                              width: 1)),
                      child: Text("问答",
                          style: TextStyle(
                            fontSize: 10.0,
                            color: Color.fromARGB(255, 7, 157, 101),
                          )))),
              Padding(
                padding: EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                child: Text("作者：小样",
                    style: TextStyle(
                      fontSize: 10.0,
                      color: Color(0xff000000),
                    )),
              ),
              Padding(
                padding: EdgeInsets.fromLTRB(0, 0, 8, 0),
                child: Text("分类：问答/官方",
                    style: TextStyle(
                      fontSize: 10.0,
                      color: Color(0xff000000),
                    )),
              ),
              Padding(
                  padding: EdgeInsets.fromLTRB(0, 0, 8, 0),
                  child: Text("时间：2020-2-20",
                      style: TextStyle(
                        fontSize: 10.0,
                        color: Color(0xff000000),
                      ))),
            ])
          ],
        )
      ]),
    );
  }
}
