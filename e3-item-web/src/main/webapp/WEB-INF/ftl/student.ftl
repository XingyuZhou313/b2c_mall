<html>
<head>
	<meta charset="UTF-8">
	<title>测试freemarker</title>
</head>
<body>
	学生信息：<br>
	学号：${student.id}&nbsp;&nbsp;姓名：${student.name}&nbsp;&nbsp;年龄：${student.age}&nbsp;&nbsp;
	家庭住址：${student.address}
	<br>
	学生列表：<br>
	<table border="1">
		<tr>
			<th>序号</th>
			<th>学号</th>
			<th>姓名</th>
			<th>年龄</th>
			<th>家庭住址</th>
		</tr>
		<#list stuList as stu>
		<#if stu_index % 2 == 0>
		<tr bgcolor="red">
		<#else>
		<tr bgcolor="blue">
		</#if>
			<td>${stu_index}</td>
			<td>${stu.id}</td>
			<td>${stu.name}</td>
			<td>${stu.age}</td>
			<td>${stu.address}</td>
		</tr>
		</#list>
	</table>
	<br>
	取当前日期：${date?date}
	<br>
	取当前时间：${date?time}
	<br>
	取当前日期+时间：${date?datetime}
	<br>
	自定义日期格式：${date?string("yyyy/MM/dd HH:mm:ss")}
	<br>
	null值的处理：${val!"val的值为null"}
	<br>
	判断val是否为null：
	<#if val??>
		val的值不为null
	<#else>
		val的值为null
	</#if>
	<br>
	加载外部模板：
	<#include "hello.ftl">
</body>
</html>