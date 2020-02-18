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
    return Row(children: <Widget>[
      Padding(
          padding: EdgeInsets.all(10.0),
          child: Image.asset(
            "assets/image/timeline_like_normal.png",
            width: 15.0,
            height: 15.0,
          )),
      Column(
        children: <Widget>[
          Text(
            "测试文字",
            style: TextStyle(fontSize: 12.0, color: Color(0xff000000)),
          ),
          Row(children: <Widget>[
            Text("测试",
                style: TextStyle(
                  fontSize: 12.0,
                  color: Color(0xff000000),
                ))
          ])
        ],
      )
    ]);
  }
}
