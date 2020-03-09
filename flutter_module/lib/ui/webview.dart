import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';

class WebView extends StatefulWidget {
  final String url;
  final String title;

  WebView({this.url, this.title});

  @override
  _WebViewState createState() => _WebViewState();
}

class _WebViewState extends State<WebView> {
  final webViewReference = FlutterWebviewPlugin();
  StreamSubscription<String> _onUrlChanged;
  StreamSubscription<WebViewStateChanged> _onStateChange;
  StreamSubscription<WebViewHttpError> _onHttpError;

  @override
  void initState() {
    super.initState();
    webViewReference.close();
    _onUrlChanged = webViewReference.onUrlChanged.listen((String url) {});
    _onStateChange = webViewReference.onStateChanged
        .listen((WebViewStateChanged stateChange) {
      switch (stateChange.type) {
        case WebViewState.startLoad:
          break;
        default:
      }
    });
    _onHttpError =
        webViewReference.onHttpError.listen((WebViewHttpError error) {});
  }

  @override
  void dispose() {
    _onStateChange.cancel();
    _onUrlChanged.cancel();
    _onHttpError.cancel();
    webViewReference.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: <Widget>[
          _appBar(),
          Expanded(
              child: WebviewScaffold(
            url: widget.url,
            withZoom: true,
            withLocalStorage: true,
            hidden: true,
            initialChild: Container(
                color: Colors.white,
                child: Center(
                  child: Text("加载中..."),
                )),
          ))
        ],
      ),
    );
  }

  Widget _appBar() {
    return Container(
      height: 80,
      decoration: BoxDecoration(color: Color(int.parse("0XFF4F94F3"))),
      child: FractionallySizedBox(
          widthFactor: 1,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              GestureDetector(
                onTap: () {
                  Navigator.pop(context);
                },
                child: Padding(
                    padding: EdgeInsets.only(left: 10, top: 10),
                    child:
                        Icon(Icons.backspace, color: Colors.white, size: 26)),
              ),
              Expanded(
                  child: Center(
                      child: Padding(
                          padding: EdgeInsets.only(top: 10, left: 10),
                          child: Text(
                            widget.title,
                            style: TextStyle(color: Colors.white, fontSize: 20),
                            overflow: TextOverflow.ellipsis,
                          ))))
            ],
          )),
    );
  }
}
