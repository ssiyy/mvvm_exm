import 'package:flutter_module/data/tag.dart';
import 'package:json_annotation/json_annotation.dart';

part 'problem.g.dart';

//https://wanandroid.com/wenda/list/1/json

@JsonSerializable()
class ProblemPage {
  final int curPage;
  final int offset; //":0,
  final bool over; //":false,
  final int pageCount; //":4,
  final int size; //":21,
  final int total; //":80
  final List<Problem> datas;

  ProblemPage(this.curPage, this.offset, this.over, this.pageCount, this.size,
      this.total, this.datas);

  factory ProblemPage.fromJson(Map<String, dynamic> json) =>
      _$ProblemPageFromJson(json);

  Map<String, dynamic> toJson() => _$ProblemPageToJson(this);
}

@JsonSerializable()
class Problem {
  final String apkLink; //:"",
  final int audit; //:1,
  final String author; //:"xiaoyang",
  final bool canEdit; //:false,
  final int chapterId; //:440,
  final String chapterName; //:"官方",
  final bool collect; //:false,
  final int courseId; //:13,
  final String desc;
  final String descMd; //",
  final String envelopePic; //",
  final bool fresh; //":false,
  final int id; //":12148,
  final String link; //https://wanandroid.com/wenda/show/12148",
  final String niceDate; //2天前",
  final String niceShareDate; //2020-03-01 15:14",
  final String origin; //",
  final String prefix; //",
  final String projectLink; //",
  final num publishTime; //":1583249129000,
  final int selfVisible; //":0,
  final num shareDate; //":1583046877000,
  final String shareUser; //",
  final int superChapterId; //":440,
  final String superChapterName; //问答",
  final String title; //":"每日一问 RecyclerView卡片中持有的资源，到底该什么时候释放？",
  final int type; //":1,
  final int userId; //":2,
  final int visible; //":1,
  final int zan; //":11
  final List<Tag> tags;

  Problem(
      this.apkLink,
      this.audit,
      this.author,
      this.canEdit,
      this.chapterId,
      this.chapterName,
      this.collect,
      this.courseId,
      this.desc,
      this.descMd,
      this.envelopePic,
      this.fresh,
      this.id,
      this.link,
      this.niceDate,
      this.niceShareDate,
      this.origin,
      this.prefix,
      this.projectLink,
      this.publishTime,
      this.selfVisible,
      this.shareDate,
      this.shareUser,
      this.superChapterId,
      this.superChapterName,
      this.title,
      this.type,
      this.userId,
      this.visible,
      this.zan,
      this.tags); //

  factory Problem.fromJson(Map<String, dynamic> json) =>
      _$ProblemFromJson(json);

  Map<String, dynamic> toJson() => _$ProblemToJson(this);
}
