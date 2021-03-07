class Student
{
constructor()
{
this.rollNumber="";
this.name="";
this.gender="";
}
setRollNumber(rollNumber)
{
this.rollNumber=rollNumber;
}
getRollNumber()
{
return this.rollNumber;
}
setName(name)
{
this.name=name;
}
getName()
{
return this.name;
}
setGender(gender)
{
this.gender=gender;
}
getGender()
{
return this.gender;
}
};
class StudentService
{
add(whatever)
{
return $.ajax({
url: 'schoolservice/StudentService/add',
type: 'POST',
data: JSON.stringify(whatever),
contentType: 'application/json'
});
}
update(whatever)
{
return $.ajax({
url: 'schoolservice/StudentService/update',
type: 'POST',
data: JSON.stringify(whatever),
contentType: 'application/json'
});
}
delete(whatever)
{
return $.ajax({
url: 'schoolservice/StudentService/delete',
type: 'GET',
data: "rollNumber="+whatever
});
}
getAll()
{
return $.ajax({
url: 'schoolservice/StudentService/getAll',
type: 'GET'
});
}
getByRollNumber(whatever)
{
return $.ajax({
url: 'schoolservice/StudentService/getByRollNumber',
type: 'GET',
data: "rollNumber="+whatever
});
}
};
