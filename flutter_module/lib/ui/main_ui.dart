import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/api/http_utils.dart';
import 'package:flutter_module/data/problem.dart';
import 'package:flutter_module/data/reponse.dart';
import 'package:flutter_module/ui/webview.dart';

class MyApp extends StatefulWidget {
  final String initParams;

  MyApp({Key key, this.initParams}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final List<Problem> _problems = [];
  int curpage = 0;

  final ScrollController _scrollController = ScrollController();
  bool isLoadDataing = false;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(() {
      if (_scrollController.position.pixels ==
          _scrollController.position.maxScrollExtent) {
        _getMoreData();
      }
    });
    _handlrefresh();
  }

  _getMoreData() async {
    if (!isLoadDataing) {
      setState(() => isLoadDataing = true);
      getPageData(curpage + 1).then((value) {
        curpage = value.curPage;
        setState(() {
          isLoadDataing = false;
          _problems.addAll(value.datas);
        });
      }).catchError((error) {
        setState(() {
          isLoadDataing = false;
        });
      });
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  Future<ProblemPage> getPageData(int pageIndex) async {
    var result =
        await HttpService.getInstance().get("/wenda/list/$pageIndex/json");
    var reponse = Reponse.fromJson(result);

    if (reponse.isSuccess) {
      return ProblemPage.fromJson(reponse.data);
    } else {
      throw Exception(reponse.errorMsg);
    }
  }

  Future<Null> _handlrefresh() async {
    getPageData(0).then((value) {
      curpage = value.curPage;
      setState(() {
        _problems.clear();
        _problems.addAll(value.datas);
      });
    }).catchError((error) {
      setState(() => {});
    });
    return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        debugShowCheckedModeBanner: false, //去除debug旗标
        home: Scaffold(
          appBar: AppBar(
              title: Align(
            alignment: Alignment.center,
            child: Text("问答"),
          )),
          body: RefreshIndicator(
              onRefresh: _handlrefresh,
              child: ListView.builder(
                  controller: _scrollController,
                  itemCount: _problems.length + 1,
                  itemBuilder: (context, index) {
                    if (index == _problems.length) {
                      return LoadingProgressIndicator(
                        loading: isLoadDataing,
                      );
                    } else {
                      return getItem(context,index);
                    }
                  })),
        ));
  }

  Widget getItem(BuildContext context,int i) {
    return GestureDetector(
      child: ListItem(_problems[i]),
      onTap: () {
        var item = _problems[i];
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) =>
                    WebView(url: item.link, title: item.title)));
      },
    );
  }
}

class LoadingProgressIndicator extends StatelessWidget {
  final loading;

  LoadingProgressIndicator({this.loading = false});

  @override
  Widget build(BuildContext context) {
    return Visibility(
        visible: loading,
        child: Padding(
          padding: EdgeInsets.all(8),
          child: Center(
            child: Opacity(
              opacity: loading ? 1 : 0,
              child: CircularProgressIndicator(),
            ),
          ),
        ));
  }
}

class ListItem extends StatelessWidget {
  final Problem _itemDate;

  ListItem(this._itemDate);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.fromLTRB(0, 10, 0, 10),
      child: Row(children: <Widget>[
        Padding(
            padding: EdgeInsets.all(10.0),
            child: Image.asset(
              "assets/image/timeline_like_normal.png",
              width: 15.0,
              height: 15.0,
            )),
        Expanded(
            child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Padding(
              padding: EdgeInsets.fromLTRB(0, 0, 0, 6),
              child: Text(
                _itemDate.title,
                textAlign: TextAlign.left,
                style: TextStyle(fontSize: 14.0, color: Color(0xff000000)),
                overflow: TextOverflow.ellipsis,
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
                      child: Text(_itemDate.superChapterName,
                          style: TextStyle(
                            fontSize: 10.0,
                            color: Color.fromARGB(255, 7, 157, 101),
                          )))),
              Padding(
                padding: EdgeInsets.fromLTRB(0.0, 0.0, 8.0, 0.0),
                child: Text("作者：${_itemDate.author}",
                    style: TextStyle(
                      fontSize: 10.0,
                      color: Color(0xff000000),
                    )),
              ),
              Padding(
                padding: EdgeInsets.fromLTRB(0, 0, 8, 0),
                child: Text(
                    "分类：${_itemDate.superChapterName}/${_itemDate.chapterName}",
                    style: TextStyle(
                      fontSize: 10.0,
                      color: Color(0xff000000),
                    )),
              ),
              Padding(
                  padding: EdgeInsets.fromLTRB(0, 0, 8, 0),
                  child: Text("时间：${_itemDate.niceDate}",
                      style: TextStyle(
                        fontSize: 10.0,
                        color: Color(0xff000000),
                      ))),
            ])
          ],
        ))
      ]),
    );
  }
}
