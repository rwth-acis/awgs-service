<!DOCTYPE html>
<html>
    <head>
        <title>ACIS Group Working Series - Item $i.id$</title>
		<meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="description" content="$i.description$">
		
		<link href="$root$/lib/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
		
		<style type="text/css">
	
			#wrap {
				min-height: 100%;
				height: auto !important;
				height: 100%;
				margin: 50px auto -50px;
			}
			
			#description {
				margin: 20px 0;
			}
			
			label {
				font-weight: bold;
			}

		</style>
		
		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<script src="http://code.jquery.com/jquery-latest.min.js"></script>
		<script type="text/javascript" src="$root$/lib/bootstrap/js/bootstrap.min.js"></script>
		
		<script type="text/javascript">
			var _gaq = _gaq || [];
			_gaq.push(['_setAccount', 'UA-38649683-1']);
			_gaq.push(['_trackPageview']);
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
		</script>
    </head>
    <body>
        
		<div id="wrap">
			<div id="content" class="container">
				
				<h2>$i.id$</h2>
				<h3>$i.name$</h3>
				
				<p id='description'>$i.description$</p>
				
				<label for='owner'>Owner:</label><div id='owner'><a href='xmpp:$i.owner$'>$i.owner$</a></div>
				<label for='url'>URL:</label><div id='url'><a href='$i.url$'>$i.url$</a></div>
				<label for='status' >Type:</label><div id='type'>$i.typeInstance.name$</div>
				<label for='lastup' >Last Update:</label><div id='lastup'>$i.lastUpdate$</div>
				
			</div>
		</div>
		
    </body>
</html>