// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'problem.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProblemPage _$ProblemPageFromJson(Map<String, dynamic> json) {
  return ProblemPage(
    json['curPage'] as int,
    json['offset'] as int,
    json['over'] as bool,
    json['pageCount'] as int,
    json['size'] as int,
    json['total'] as int,
    (json['datas'] as List)
        ?.map((e) =>
            e == null ? null : Problem.fromJson(e as Map<String, dynamic>))
        ?.toList(),
  );
}

Map<String, dynamic> _$ProblemPageToJson(ProblemPage instance) =>
    <String, dynamic>{
      'curPage': instance.curPage,
      'offset': instance.offset,
      'over': instance.over,
      'pageCount': instance.pageCount,
      'size': instance.size,
      'total': instance.total,
      'datas': instance.datas,
    };

Problem _$ProblemFromJson(Map<String, dynamic> json) {
  return Problem(
    json['apkLink'] as String,
    json['audit'] as int,
    json['author'] as String,
    json['canEdit'] as bool,
    json['chapterId'] as int,
    json['chapterName'] as String,
    json['collect'] as bool,
    json['courseId'] as int,
    json['desc'] as String,
    json['descMd'] as String,
    json['envelopePic'] as String,
    json['fresh'] as bool,
    json['id'] as int,
    json['link'] as String,
    json['niceDate'] as String,
    json['niceShareDate'] as String,
    json['origin'] as String,
    json['prefix'] as String,
    json['projectLink'] as String,
    json['publishTime'] as num,
    json['selfVisible'] as int,
    json['shareDate'] as num,
    json['shareUser'] as String,
    json['superChapterId'] as int,
    json['superChapterName'] as String,
    json['title'] as String,
    json['type'] as int,
    json['userId'] as int,
    json['visible'] as int,
    json['zan'] as int,
    (json['tags'] as List)
        ?.map((e) => e == null ? null : Tag.fromJson(e as Map<String, dynamic>))
        ?.toList(),
  );
}

Map<String, dynamic> _$ProblemToJson(Problem instance) => <String, dynamic>{
      'apkLink': instance.apkLink,
      'audit': instance.audit,
      'author': instance.author,
      'canEdit': instance.canEdit,
      'chapterId': instance.chapterId,
      'chapterName': instance.chapterName,
      'collect': instance.collect,
      'courseId': instance.courseId,
      'desc': instance.desc,
      'descMd': instance.descMd,
      'envelopePic': instance.envelopePic,
      'fresh': instance.fresh,
      'id': instance.id,
      'link': instance.link,
      'niceDate': instance.niceDate,
      'niceShareDate': instance.niceShareDate,
      'origin': instance.origin,
      'prefix': instance.prefix,
      'projectLink': instance.projectLink,
      'publishTime': instance.publishTime,
      'selfVisible': instance.selfVisible,
      'shareDate': instance.shareDate,
      'shareUser': instance.shareUser,
      'superChapterId': instance.superChapterId,
      'superChapterName': instance.superChapterName,
      'title': instance.title,
      'type': instance.type,
      'userId': instance.userId,
      'visible': instance.visible,
      'zan': instance.zan,
      'tags': instance.tags,
    };
